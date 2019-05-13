package com.yww.utils.manager

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.Keep
import android.util.Log
import com.yww.utils.util.Util
import java.util.concurrent.ConcurrentHashMap

/**
 * @author  WAVENING
 *
 * please read me first before using
 * this class can manager itself and control shared preference in itself
 * but when we use it in development, do not make multiple manager,just keep only one you need;
 * for example, in you custom application
 * do this action
 * SpManager.initManager("your custom name")
 *
 * first ,then you can SpManager.INSTANCE to get the one SpManager you set everywhere
 *
 * ps：you may have noticed the code  {@link Util#getApp() }
 * you may can possibly get null in some kind of machine, you can {@link Util#init（application）}
 * to avoid that
 */
@Keep
class SpManager private constructor(name: String) {
    companion object {
        private val spMap: ConcurrentHashMap<String, SpManager> = ConcurrentHashMap()
        private const val TAG = "SpManager"
        var name: String = TAG
            private set


        @JvmStatic
        var instance: SpManager?
            get() {
                return spMap[name]
            }
            private set(value) {
                Log.e("aaa", "name==$name")
                if (null != value && !spMap.containsKey(name)) spMap[name] = value
                else throw IllegalAccessException("this can not set twice")
            }

        fun initManager(name: String) {
            this.name = name
            instance = SpManager(name)
        }
    }

    fun getSize() = spMap.size

    private var sharedPre: SharedPreferences = Util.getApplication()?.getSharedPreferences(name, Context.MODE_PRIVATE)
        ?: throw Throwable("shared preference cannot initiate")


    /**
     * Put the string value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     * false to use [SharedPreferences.Editor.apply]
     */
    @Keep
    fun put(key: String, value: String) {
        sharedPre.edit().putString(key, value).apply()
    }

    /**
     * Return the string value in sp.
     *
     * @param key The key of sp.
     * @return the string value if sp exists or `""` otherwise
     */
    @Keep
    fun getString(key: String): String? {
        return getString(key, "")
    }

    /**
     * Return the string value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the string value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getString(key: String, defaultValue: String): String? {
        return sharedPre.getString(key, defaultValue)
    }

    /**
     * Put the int value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     */
    @Keep
    fun put(key: String, value: Int) {
        sharedPre.edit().putInt(key, value).apply()
    }

    /**
     * Return the int value in sp.
     *
     * @param key The key of sp.
     * @return the int value if sp exists or `-1` otherwise
     */
    @Keep
    fun getInt(key: String): Int {
        return getInt(key, -1)
    }

    /**
     * Return the int value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the int value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPre.getInt(key, defaultValue)
    }


    /**
     * Put the long value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     */
    @Keep
    fun put(key: String, value: Long) {
        sharedPre.edit().putLong(key, value).apply()
    }

    /**
     * Return the long value in sp.
     *
     * @param key The key of sp.
     * @return the long value if sp exists or `-1` otherwise
     */
    @Keep
    fun getLong(key: String): Long {
        return getLong(key, -1L)
    }

    /**
     * Return the long value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the long value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPre.getLong(key, defaultValue)
    }

    /**
     * Put the float value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     */
    @Keep
    fun put(key: String, value: Float) {
        sharedPre.edit().putFloat(key, value).apply()
    }

    /**
     * Return the float value in sp.
     *
     * @param key The key of sp.
     * @return the float value if sp exists or `-1f` otherwise
     */
    @Keep
    fun getFloat(key: String): Float {
        return getFloat(key, -1f)
    }

    /**
     * Return the float value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the float value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPre.getFloat(key, defaultValue)
    }

    /**
     * Put the boolean value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     */
    @Keep
    fun put(key: String, value: Boolean) {
        sharedPre.edit().putBoolean(key, value).apply()
    }

    /**
     * Return the boolean value in sp.
     *
     * @param key The key of sp.
     * @return the boolean value if sp exists or `false` otherwise
     */
    @Keep
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * Return the boolean value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the boolean value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPre.getBoolean(key, defaultValue)
    }

    /**
     * Put the set of string value in sp.
     *
     * @param key      The key of sp.
     * @param value    The value of sp.
     * @param isCommit True to use [SharedPreferences.Editor.commit],
     * false to use [SharedPreferences.Editor.apply]
     */
    @Keep
    fun put(key: String, value: Set<String>) {
        sharedPre.edit().putStringSet(key, value).apply()
    }

    /**
     * Return the set of string value in sp.
     *
     * @param key The key of sp.
     * @return the set of string value if sp exists
     * or `Collections.<String>emptySet()` otherwise
     */
    @Keep
    fun getStringSet(key: String): Set<String>? {
        return getStringSet(key, emptySet())
    }

    /**
     * Return the set of string value in sp.
     *
     * @param key          The key of sp.
     * @param defaultValue The default value if the sp doesn't exist.
     * @return the set of string value if sp exists or `defaultValue` otherwise
     */
    @Keep
    fun getStringSet(key: String, defaultValue: Set<String>): Set<String>? {
        return sharedPre.getStringSet(key, defaultValue)
    }

    /**
     * Return all values in sp.
     *
     * @return all values in sp
     */
    @Keep
    fun getAll(): Map<String, *> {
        return sharedPre.getAll()
    }

    /**
     * Return whether the sp contains the preference.
     *
     * @param key The key of sp.
     * @return `true`: yes<br></br>`false`: no
     */
    @Keep
    operator fun contains(key: String): Boolean {
        return sharedPre.contains(key)
    }


    /**
     * Remove the preference in sp.
     *
     * @param key      The key of sp.
     * @param isCommit True to use [SharedPreferences.Editor.commit],
     * false to use [SharedPreferences.Editor.apply]
     */
    @Keep
    fun remove(key: String) {
        sharedPre.edit().remove(key).apply()
    }

    /**
     * Remove all preferences in sp.
     */
    @Keep
    fun clear() = sharedPre.edit().clear().apply()

    private fun isAllSpace(name: CharSequence?): Boolean {
        if (null == name) return true
        (0 until name.length).forEach {
            if (!Character.isWhitespace(name[it])) return false
        }
        return true
    }
}