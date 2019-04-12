package com.yww.utils.annotation

import android.Manifest.permission
import android.annotation.SuppressLint
import android.os.Build
import com.yww.utils.R
import com.yww.utils.extension.*


/**
 * @Author WAVENING
 */
@SuppressLint("InlinedApi")
internal class PermissionConst {

    private object Holder {
        val INSTANCE = PermissionConst()
    }

    companion object {
        val instance: PermissionConst = Holder.INSTANCE
    }

    private val calendar: Set<String> = setOf(permission.READ_CALENDAR, permission.WRITE_CALENDAR)
    private val camera: Set<String> = setOf(permission.CAMERA)
    private val contacts: Set<String> =
        setOf(permission.READ_CONTACTS, permission.WRITE_CONTACTS, permission.GET_ACCOUNTS)

    private val location: Set<String> =
        setOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)

    private val microphone: Set<String> = setOf(permission.RECORD_AUDIO)
    private val phone: MutableSet<String> = mutableSetOf(
        permission.READ_PHONE_STATE,
        permission.READ_PHONE_NUMBERS,
        permission.CALL_PHONE,
        permission.READ_CALL_LOG,
        permission.WRITE_CALL_LOG,
        permission.ADD_VOICEMAIL,
        permission.USE_SIP,
        permission.PROCESS_OUTGOING_CALLS,
        permission.ANSWER_PHONE_CALLS
    )
    private val phoneBelowO: Set<String> = phone.minus(phone.last())
    private val sensor: Set<String> = setOf(permission.BODY_SENSORS)
    private val sms: Set<String> = setOf(
        permission.SEND_SMS,
        permission.RECEIVE_SMS,
        permission.READ_SMS,
        permission.RECEIVE_WAP_PUSH,
        permission.RECEIVE_MMS
    )
    private val storage: Set<String> =
        setOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)

    fun getGroupPermissionsByPermissionName(permissions: Set<String>): Set<String> {
        val permissionGroupSet: Set<String> = getPermissionGroup(permissions)
        val permissionSet: MutableSet<String> = mutableSetOf()
        val iterator = permissionGroupSet.iterator()
        while (iterator.hasNext()) {
            permissionSet.addAll(getPermissionsByPermissionGroup(iterator.next()))
        }
        return permissionSet
    }

    private fun getPermissionsByPermissionGroup(@PermissionAnnotation permissionGroup: String): Set<String> =
        when (permissionGroup) {
            calendarGroup -> calendar
            cameraGroup -> camera
            contactsGroup -> contacts
            locationGroup -> location
            microphoneGroup -> microphone
            phoneGroup -> when (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                true -> phoneBelowO
                false -> phone
            }
            sensorGroup -> sensor
            smsGroup -> sms
            storageGroup -> storage
            undefinedGroup -> emptySet()
            else -> emptySet()
        }


    private fun getPermissionGroup(permissions: Set<String>): Set<String> {
        val permissionGroupSet: MutableSet<String> = mutableSetOf()
        val iterator = permissions.iterator()
        while (iterator.hasNext()) {
            permissionGroupSet.add(findPermissionGroup(iterator.next()))
        }
        return permissionGroupSet
    }


    private fun findPermissionGroup(permission: String): String =
        when (true) {
            permission in calendar -> calendarGroup
            permission in camera -> cameraGroup
            permission in contacts -> contactsGroup
            permission in location -> locationGroup
            permission in microphone -> microphoneGroup
            permission in phone -> phoneGroup
            permission in sensor -> sensorGroup
            permission in sms -> smsGroup
            permission in storage -> storageGroup
            else -> undefinedGroup
        }

    fun getPermissionDescription(permissions: Set<String>): Set<String> {
        val permissionSet: MutableSet<String> = mutableSetOf()
        val iterator = permissions.iterator()
        while (iterator.hasNext()) {
            permissionSet.add(findPermissionsDescriptionByPermissionGroupName(findPermissionGroup(iterator.next())))
        }
        return permissionSet
    }

    private fun findPermissionsDescriptionByPermissionGroupName(@PermissionAnnotation permissionGroup: String): String =
        when (permissionGroup) {
            calendarGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_calendar_description)
            cameraGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_camera_description)
            contactsGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_contacts_description)
            locationGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_location_description)
            microphoneGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_microphone_description)
            phoneGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_phone_description)
            sensorGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_sensor_description)
            smsGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_sms_description)
            storageGroup -> getPermissionGroupDescriptionByStringId(R.string.text_permission_const_storage_description)
            undefinedGroup -> ""
            else -> ""
        }

}