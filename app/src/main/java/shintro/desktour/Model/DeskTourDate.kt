package shintaro.desktour_cluod_firestore

import java.util.*


data class DeskTourDate(
    val title: String, val timestamp: Date, val comment: String,
    val numLikes: Int, val NumComments: Int, val documentId: String)