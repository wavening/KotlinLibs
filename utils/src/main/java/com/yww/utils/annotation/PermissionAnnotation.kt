package com.yww.utils.annotation

import android.support.annotation.StringDef
import com.yww.utils.extension.*

/**
 * @Author  WAVENING
 * @Date    2019/4/12-15:00
 */
@StringDef(
    value = [calendarGroup, cameraGroup, contactsGroup, locationGroup, microphoneGroup,
        phoneGroup, sensorGroup, smsGroup, storageGroup, undefinedGroup]
)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class PermissionAnnotation