package im.status.keycard.connect.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Base64.NO_PADDING
import android.util.Base64.NO_WRAP
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import im.status.keycard.applet.Pairing

class PairingManager(context: Context) {
    private val sharedPreferences: SharedPreferences

    private fun id(instanceUID: ByteArray) : String {
        return Base64.encodeToString(instanceUID, NO_PADDING or NO_WRAP)
    }

    init {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sharedPreferences = EncryptedSharedPreferences.create(context,"pairings", masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    fun getPairing(instanceUID: ByteArray): Pairing? {
        val p = sharedPreferences.getString(id(instanceUID), null)
        return if (p != null) Pairing(p) else null
    }

    fun putPairing(instanceUID: ByteArray, pairing: Pairing) {
        sharedPreferences.edit().apply {
            putString(id(instanceUID), pairing.toBase64())
            apply()
        }
    }

    fun removePairing(instanceUID: ByteArray) {
        sharedPreferences.edit().apply {
            remove(id(instanceUID))
            apply()
        }
    }
}