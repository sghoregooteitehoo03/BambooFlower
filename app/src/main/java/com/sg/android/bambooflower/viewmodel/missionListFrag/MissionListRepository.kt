package com.sg.android.bambooflower.viewmodel.missionListFrag

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

class MissionListRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    @Named(Contents.PREF_CHECK_FIRST) private val checkPref: SharedPreferences
) {

    // 홈 데이터
    fun getHomeData(isLoadMission: Boolean): Task<HttpsCallableResult> {
        val jsonData = JSONObject().apply {
            put("isLoadMission", isLoadMission)
            put("uid", auth.currentUser?.uid)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_HOME_DATA)
            .call(jsonData)
    }

    // 미션 바꾸기
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

    fun checkFirst() {
        if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            with(checkPref.edit()) {
                putBoolean(com.sg.android.bambooflower.other.Contents.PREF_KEY_IS_FIRST, false)
                commit()
            }
        }
    }
}