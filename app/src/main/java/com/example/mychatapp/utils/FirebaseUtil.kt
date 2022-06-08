package com.example.mychatapp.utils

import android.net.Uri
import android.util.Log
import com.example.mychatapp.model.Message
import com.example.mychatapp.model.Room
import com.example.mychatapp.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun addMember(roomId: String, uid: String) {
    return withContext(Dispatchers.IO) {
        Log.d("xxx", "xxx")
        val roomRef = Firebase.firestore.collection("rooms")
            .document(roomId)
        val userSnapshot = Firebase.firestore.collection("users")
            .document(uid).get().await()
        Firebase.firestore.runBatch {
            it.update(roomRef, "members", FieldValue.arrayUnion(uid))
            it.set(
                roomRef.collection("membersInfo").document(uid),
                mapOf(
                    "uid" to uid,
                    "nickname" to userSnapshot.getString("nickname"),
                    "displayName" to userSnapshot.getString("displayName"),
                    "photoUrl" to userSnapshot.getString("photoUrl"),
                    "role" to "member"
                )
            )
        }.await()
    }
}

suspend fun deleteMember(roomId: String, uid: String) {
    return withContext(Dispatchers.IO) {
        val roomRef = Firebase.firestore.collection("rooms")
            .document(roomId)
        Firebase.firestore.runBatch {
            it.update(roomRef, "members", FieldValue.arrayRemove(uid))
            it.delete(roomRef.collection("membersInfo").document(uid))
        }
    }
}

suspend fun createRoom(name: String?, description: String?): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val uid = Firebase.auth.currentUser!!.uid
            val roomRef = Firebase.firestore.collection("rooms").add(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "lastMessage" to null,
                    "lastMessageTime" to null,
                )
            ).await()
            val meSnapshot = Firebase.firestore.collection("users").document(uid)
                .get().await()
            roomRef.collection("membersInfo").document(uid)
                .set(
                    mapOf(
                        "uid" to uid,
                        "displayName" to meSnapshot.getString("displayName"),
                        "nickname" to meSnapshot.getString("nickname"),
                        "photoUrl" to meSnapshot.getString("photoUrl"),
                        "role" to "admin"
                    )
                ).await()
            roomRef
                .update(
                    "members", FieldValue.arrayUnion(uid)
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

suspend fun getRoom(id: String): Room? {
    return withContext(Dispatchers.IO) {
        try {
            val snapshot = Firebase.firestore.collection("rooms")
                .document(id).get().await()
            Room(snapshot.id, snapshot.getString("name"), snapshot.getString("description"))
        } catch (e: Exception) {
            null
        }
    }
}

suspend fun createMessage(roomId: String, text: String?, imageUrl: String?): DocumentReference? {
    return withContext(Dispatchers.IO) {
        try {
            val uid = Firebase.auth.currentUser!!.uid
            val userSnapshot = Firebase.firestore.collection("users")
                .document(uid)
                .get().await()
            val user = User(
                uid,
                userSnapshot.getString("nickname"),
                userSnapshot.getString("displayName"),
                userSnapshot.getString("bio"),
                userSnapshot.getString("photoUrl"),
            )
            val message = Message(
                uid,
                user.displayName,
                user.photoUrl,
                text,
                imageUrl,
                Timestamp.now()
            )
            Firebase.firestore.collection("rooms")
                .document(roomId)
                .update(
                    mapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.sendTimestamp,
                    )
                )
            Firebase.firestore.collection("rooms")
                .document(roomId)
                .collection("chats")
                .add(message)
                .await()

        } catch (e: Exception) {
            null
        }
    }
}

suspend fun createFriendRequest(uid: String, user: User): Boolean {
    return try {
        Firebase.firestore.collection("users")
            .document(uid)
            .collection("friendRequests")
            .document(user.uid!!)
            .set(user)
            .await()
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun deleteFriendRequest(meId: String, userId: String): Boolean {
    return try {
        Firebase.firestore.collection("users")
            .document(meId)
            .collection("friendRequests")
            .document(userId)
            .delete()
            .await()
        true
    } catch (e: Exception) {
        false

    }
}

suspend fun confirmFriend(me: User, user: User): Boolean {
    return try {
        Firebase.firestore.runBatch {
            it.delete(
                Firebase.firestore.collection("users").document(me.uid!!)
                    .collection("friendRequests").document(user.uid!!)
            )
            it.set(
                Firebase.firestore.collection("users").document(me.uid)
                    .collection("friends").document(user.uid),
                user
            )
            it.set(
                Firebase.firestore.collection("users").document(user.uid)
                    .collection("friends").document(me.uid),
                me
            )
        }.await()
        true
    } catch (e: Exception) {
        false
    }

}

suspend fun findUserByNickname(nickname: String): DocumentSnapshot? {
    return try {
        val querySnapshot = Firebase.firestore.collection("users")
            .whereEqualTo("nickname", nickname)
            .limit(1)
            .get()
            .await()
        querySnapshot.documents.get(0)
    } catch (e: Exception) {
        null
    }
}

suspend fun updateCurrentUser(user: User): Boolean {
    return try {
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .update(
                mapOf(
                    "displayName" to user.displayName,
                    "bio" to user.bio,
                )
            ).await()
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun getCurrentUser(): User? {
    return try {
        val uid = Firebase.auth.currentUser!!.uid
        val doc = Firebase.firestore.collection("users")
            .document(uid)
            .get().await()
        User(
            uid = uid,
            nickname = doc.getString("nickname"),
            displayName = doc.getString("displayName"),
            bio = doc.getString("bio"),
            photoUrl = doc.getString("photoUrl"),
        )

    } catch (e: Exception) {
        null
    }
}

suspend fun uploadFile(file: Uri, ref: String): UploadTask.TaskSnapshot? {
    return try {
        Firebase.storage.reference.child(ref)
            .putFile(file)
            .await()
    } catch (e: Exception) {
        null
    }
}
