package shintro.desktour

import java.util.*

data class DeskTour constructor(val title: String, val timestamp: Date, val comment: String,
                               val numLikes: Int, val NumComments: Int, val documentId: String)