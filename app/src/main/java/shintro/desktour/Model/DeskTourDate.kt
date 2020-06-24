package shintaro.desktour_cluod_firestore

import java.util.*


data class DeskTourDate(
    val timestamp: String, val title: String, val thoughtTxt: Date,
    val numLikes: Int, val NumComments: Int, val documentId: String)