package com.zjgsu.ifm_preview.data.model

import android.os.Parcel
import android.os.Parcelable

data class Podcast(
    val id: String,
    val title: String,
    val description: String,
    val coverImage: String,
    val author: String,
    val category: String,
    val episodes: List<Episode>,
    val isSubscribed: Boolean = false,
    val subscriptionCount: Int = 0
) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Episode.CREATOR) ?: emptyList(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(coverImage)
        parcel.writeString(author)
        parcel.writeString(category)
        parcel.writeTypedList(episodes)
        parcel.writeByte(if (isSubscribed) 1 else 0)
        parcel.writeInt(subscriptionCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Podcast> {
        override fun createFromParcel(parcel: Parcel): Podcast {
            return Podcast(parcel)
        }

        override fun newArray(size: Int): Array<Podcast?> {
            return arrayOfNulls(size)
        }
    }

    fun isValid(): Boolean {
        return id.isNotBlank() && title.isNotBlank() && coverImage.isNotBlank() && author.isNotBlank()
    }
}

data class Episode(
    val id: String,
    val podcastId: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val duration: String,
    val publishDate: String,
    val isPlayed: Boolean = false,
    val playProgress: Int = 0
) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(podcastId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(audioUrl)
        parcel.writeString(duration)
        parcel.writeString(publishDate)
        parcel.writeByte(if (isPlayed) 1 else 0)
        parcel.writeInt(playProgress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Episode> {
        override fun createFromParcel(parcel: Parcel): Episode {
            return Episode(parcel)
        }

        override fun newArray(size: Int): Array<Episode?> {
            return arrayOfNulls(size)
        }
    }

    fun getFormattedDuration(): String {
        return try {
            val parts = duration.split(":")
            when (parts.size) {
                3 -> {
                    val hours = parts[0].toInt()
                    val minutes = parts[1].toInt()
                    if (hours > 0) "${hours}小时${minutes}分钟" else "${minutes}分钟"
                }
                2 -> {
                    val minutes = parts[0].toInt()
                    "${minutes}分钟"
                }
                else -> duration
            }
        } catch (e: Exception) {
            duration
        }
    }
    
    fun isPlayable(): Boolean {
        return audioUrl.isNotBlank() && audioUrl.startsWith("http")
    }
}
