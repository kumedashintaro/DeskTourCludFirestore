package shintro.desktour

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Desk(var uid: String = "", var titel: String = "", var comment: String = "", var profileImageUrl: String = "" , var deskuid: String = ""): Parcelable