package shintaro.desktour_cluod_firestore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var uid: String = "", var username: String = "", var profileImageUrl: String = ""): Parcelable
