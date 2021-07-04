package com.sg.android.bambooflower.viewmodel.missionFrag

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

class MissionRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    @Named(Contents.PREF_CHECK_FIRST) private val checkPref: SharedPreferences
) {

    fun getHomeData(isLoadMission: Boolean): Task<HttpsCallableResult> {
        val jsonData = JSONObject().apply {
            put("isLoadMission", isLoadMission)
            put("uid", auth.currentUser?.uid)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_HOME_DATA)
            .call(jsonData)
    }

    suspend fun changeMission(user: User, missions: List<Mission>): HttpsCallableResult {
        val missionsJson = JSONArray().apply {
            for (mission in missions) {
                put(Gson().toJson(mission))
            }
        }
        val jsonData = JSONObject().apply {
            put("user", Gson().toJson(user))
            put("missions", missionsJson)
        }

        return functions.getHttpsCallable(Contents.FUNC_CHANGE_MISSION)
            .call(jsonData)
            .await()!!
    }

    // 작성했던 게시글을 찾음
    suspend fun searchPost(mission: Mission, user: User): Post? {
        return if (mission.document != user.missionDoc
            && !mission.complete.containsKey(user.uid)
        ) { // 유저가 수행중인 미션이 아니고 미수행한 미션일 경우 데이터베이스를 읽지 않음
            null
        } else {
            val post = store.collection(Contents.COLLECTION_POST)
                .whereEqualTo("uid", user.uid)
                .whereEqualTo("missionDoc", mission.document)
                .get()
                .await()

            if (post.isEmpty) { // 작성한 게시글이 없을 때
                null
            } else { // 작성한 게시글이 있을 때
                post.documents[0].toObject(Post::class.java)
            }
        }
    }

    fun checkFirst() {
        if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            with(checkPref.edit()) {
                putBoolean(com.sg.android.bambooflower.other.Contents.PREF_KEY_IS_FIRST, false)
                commit()
            }
        }
    }
}