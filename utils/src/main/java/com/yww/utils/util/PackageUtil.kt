package com.yww.utils.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.*
import android.os.Build
import android.os.Bundle
import com.yww.utils.extension.application

/**
 * @Author  WAVENING
 * @Date    2019/3/21-9:51
 */

object PackageUtil {
    fun getPackageManager(): PackageManager = application?.packageManager!!
    fun getPackageManager(context: Context): PackageManager = context.packageManager!!

    /**
     * find package info according to package name settled in
     */
    @Synchronized
    private fun findPackageInfoByPackageNameAndExactFlag(packageName: String, flag: Int): PackageInfo =
        getPackageManager().getPackageInfo(packageName, flag)

    /**
     * find all activities in the package by package name
     */
    fun findActivitiesInPackage(packageName: String): MutableList<ActivityInfo> =
        findPackageInfoByPackageNameAndExactFlag(packageName, PackageManager.GET_ACTIVITIES).activities.toMutableList()

    /**
     * find all receivers in the package by package name
     */
    fun findReceiversInPackage(packageName: String): MutableList<ActivityInfo> =
        findPackageInfoByPackageNameAndExactFlag(packageName, PackageManager.GET_RECEIVERS).receivers.toMutableList()

    /**
     * find all services in the package by package name
     */
    fun findServicesInPackage(packageName: String): MutableList<ServiceInfo> =
        findPackageInfoByPackageNameAndExactFlag(packageName, PackageManager.GET_SERVICES).services.toMutableList()

    /**
     * find all providers in the package by package name
     */
    fun findProvicersInPackage(packageName: String): MutableList<ProviderInfo> =
        findPackageInfoByPackageNameAndExactFlag(packageName, PackageManager.GET_PROVIDERS).providers.toMutableList()

    /**
     * find all providers in the package by package name
     */

    @SuppressLint("NewApi")
    fun findSignatureInPackage(packageName: String): Any = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        true -> findPackageInfoByPackageNameAndExactFlag(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo
        false -> findPackageInfoByPackageNameAndExactFlag(packageName, PackageManager.GET_SIGNATURES).signatures
    }


    /**
     * find all permissions in the package by package name
     */
    fun findPermissionsInPackage(packageName: String): MutableList<PermissionInfo> =
        findPackageInfoByPackageNameAndExactFlag(
            packageName,
            PackageManager.GET_PERMISSIONS
        ).permissions.toMutableList()

    /**
     * find requested permissions in the package by package name
     */
    fun findRequestedPermissionsInPackageManifest(packageName: String): MutableList<String> =
        findPackageInfoByPackageNameAndExactFlag(
            packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions.toMutableList()

    /**
     * find shared library files in the package by package name
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun findSharedLibraryFilesInPackage(packageName: String): MutableList<String> =
        (findPackageInfoByPackageNameAndExactFlag(
            packageName,
            PackageManager.GET_SHARED_LIBRARY_FILES
        ) as ApplicationInfo).sharedLibraryFiles.toMutableList()

    /**
     * find meta data permissions in the package by package name
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun findMetaDataInPackageManifest(packageName: String): Bundle =
        (findPackageInfoByPackageNameAndExactFlag(
            packageName,
            PackageManager.GET_META_DATA
        ) as ComponentInfo).metaData

    /**
     * find all installed packages in the machine
     */
    val findAllInstalledPackagesInMachine: MutableList<PackageInfo> =
        getPackageManager().getInstalledPackages(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PackageManager.MATCH_UNINSTALLED_PACKAGES
            } else {
                PackageManager.GET_UNINSTALLED_PACKAGES
            }
        )

    val findAllCurrentInstalledPackagesInMachine: MutableList<PackageInfo> =
        getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES)

}