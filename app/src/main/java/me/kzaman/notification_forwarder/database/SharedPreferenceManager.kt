package me.kzaman.notification_forwarder.database

import android.content.SharedPreferences
import me.kzaman.notification_forwarder.data.model.TokenDataModel
import me.kzaman.notification_forwarder.data.model.UserDataModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SharedPreferenceManager @Inject constructor(
    private val preferences: SharedPreferences,
) {

    companion object {
        const val areaInfo = "area_info" //store login information
        const val ACCESS_TOKEN = "token" //store token information
        const val IS_LOGIN = "is_login" //store token information
        const val IS_REMEMBER = "is_remember"
        const val REM_USER_NAME = "remembered_user_name"
        const val REM_PASSWORD = "remembered_password"

        // user info
        const val USER_ID = "userId"
        const val HASH_UID = "hashUid"
        const val USER_TYPE = "userType"
        const val USER_NAME = "userName"
        const val FULL_NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val PHOTO = "photo"
        const val ROLE_ID = "roleId"
        const val ROLE_NAME = "roleName"
        const val VISIBLE_NAME = "visibleName"
        const val SUB_ID = "subId"
        const val SUB_NAME = "sbuName"
    }

    /**
     * Store user name and password
     * @param isRemember check box status, when true store user credential, otherwise not
     * @param userName entered user name
     * @param password entered password
     */
    fun rememberUserCredential(isRemember: Boolean, userName: String, password: String) {
        preferences[IS_REMEMBER] = isRemember
        preferences[REM_USER_NAME] = userName
        preferences[REM_PASSWORD] = password
    }

    /**
     * ...update password
     * ...update username
     * @param password & username update
     */
    fun updateRememberUserCredential(userName: String, password: String) {
        preferences[REM_USER_NAME] = userName
        preferences[REM_PASSWORD] = password
    }

    /**
     * get user remember status
     * @return if remember then return true otherwise false
     */
    fun isRemembered(): Boolean {
        return preferences[IS_REMEMBER] ?: false
    }

    /**
     * get last remembered user name
     * @return user name
     */
    fun getRememberUsername(): String {
        return preferences[REM_USER_NAME] ?: ""
    }

    /**
     * get last remembered password
     * @return user password
     */
    fun getRememberPassword(): String {
        return preferences[REM_PASSWORD] ?: ""
    }

    /**
     * save is user logged in
     * @param isLoggedIn log in status
     */
    fun setLoginStatus(isLoggedIn: Boolean) {
        preferences[IS_LOGIN] = isLoggedIn
    }

    /**
     * save logged in user information
     * @param user logged in user info
     */
    fun setUserInformation(user: UserDataModel) {
        preferences[USER_ID] = user.id
        preferences[HASH_UID] = user.userId
        preferences[USER_TYPE] = user.userType
        preferences[USER_NAME] = user.userName
        preferences[FULL_NAME] = user.name
        preferences[EMAIL] = user.email
        preferences[PHONE] = user.phone
        preferences[ROLE_ID] = user.roleId
        preferences[ROLE_NAME] = user.roleName
        preferences[VISIBLE_NAME] = user.visibleName
        preferences[SUB_ID] = user.sbuId
        preferences[SUB_NAME] = user.sbuName
    }

    fun setTokenInformation(tokenDataModel: TokenDataModel) {
//        editor.putString("accessToken", tokenDataModel.accessToken)
//        editor.putString("tokenType", tokenDataModel.tokenType)
//        editor.putString("expireDate", tokenDataModel.expireAt)
        preferences[ACCESS_TOKEN] = tokenDataModel.accessToken
    }

    /**
     * ...get current user name
     * @return user name
     */
    fun getUserName(): String {
        return preferences[USER_NAME] ?: ""
    }

    /**
     * get user login status
     * @return true if user logged in otherwise return false
     */
    fun getIsUserLoggedIn(): Boolean {
        return preferences[IS_LOGIN] ?: false
    }

    /**
     * ...get user access token
     * @return user access token
     */
    fun getAccessToken(): String {
        return preferences[ACCESS_TOKEN] ?: ""
    }

    /**
     * ...get user full name
     * @return user full name
     */
    fun getUserFullName(): String {
        return preferences[FULL_NAME] ?: ""
    }

    /**
     * ...get user role name
     * @return user role name
     */
    fun getUserRoleName(): String {
        return preferences[VISIBLE_NAME] ?: ""
    }

    /**
     * ...clear all data while user logged out
     */
    fun clearAll() {
        preferences[IS_LOGIN] = false
        preferences[ACCESS_TOKEN] = null
        preferences[VISIBLE_NAME] = null
        preferences[USER_NAME] = null
        preferences[FULL_NAME] = null
    }
}

/**
 * SharedPreferences extension function, to listen the edit() and apply() fun calls
 * on every SharedPreferences operation.
 */
private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
private operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
private inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null,
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}