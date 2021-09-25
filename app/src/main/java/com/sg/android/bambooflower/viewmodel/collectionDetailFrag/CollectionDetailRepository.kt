package com.sg.android.bambooflower.viewmodel.collectionDetailFrag

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CollectionDetailRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    suspend fun getInventoryData(uid: String): HttpsCallableResult =
        functions.getHttpsCallable(Contents.FUNC_GET_COLLECTION_DATA)
            .call(uid)
            .await()!!

}