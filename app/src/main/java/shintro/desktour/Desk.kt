package shintro.desktour

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Desk(val uid: String="", val titel: String ="", val comment: String ="", val profileImageUrl: String ="", val deskuid: String =""): Parcelable