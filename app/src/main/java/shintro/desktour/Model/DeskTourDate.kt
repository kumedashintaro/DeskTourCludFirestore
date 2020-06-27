package shintaro.desktour_cluod_firestore

import java.util.*


data class DeskTourDate(
    val title: String, val comment: String, val timestamp: Date,
    val numLikes: Int, val NumComments: Int, val documentId: String)