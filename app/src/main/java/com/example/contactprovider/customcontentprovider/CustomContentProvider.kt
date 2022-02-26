package com.example.contactprovider.customcontentprovider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.squareup.moshi.Moshi

class CustomContentProvider : ContentProvider() {

    private lateinit var userPrefs: SharedPreferences
    private lateinit var coursesPrefs: SharedPreferences

    private val userAdapter = Moshi.Builder().build().adapter(User::class.java)
    private val courseAdapter = Moshi.Builder().build().adapter(Course::class.java)

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITIES, PATH_USERS, TYPE_USERS)
        addURI(AUTHORITIES, PATH_COURSES, TYPE_COURSES)
        addURI(AUTHORITIES, "$PATH_USERS/#", TYPE_USER_ID)
        addURI(AUTHORITIES, "$PATH_COURSES/#", TYPE_COURSE_ID)
    }

    override fun onCreate(): Boolean {
        userPrefs = context!!.getSharedPreferences("user_shared_prefs", Context.MODE_PRIVATE)
        coursesPrefs = context!!.getSharedPreferences("course_shared_prefs", Context.MODE_PRIVATE)
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        return when (uriMatcher.match(p0)) {
            TYPE_USERS -> getAllUsersCursor()
            TYPE_COURSES -> getAllCoursesCursor()
            else -> null
        }
    }

    private fun getAllUsersCursor(): Cursor {
        val allUsers = userPrefs.all.mapNotNull {
            val userJsonString = it.value as String
            userAdapter.fromJson(userJsonString)
        }

        val cursor = MatrixCursor(arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_AGE))
        allUsers.forEach {
            cursor.newRow()
                .add(it.id)
                .add(it.name)
                .add(it.age)
        }
        return cursor
    }

    private fun getAllCoursesCursor(): Cursor {
        val allCourses = coursesPrefs.all.mapNotNull {
            val courseJsonString = it.value as String
            courseAdapter.fromJson(courseJsonString)
        }

        val cursor = MatrixCursor(arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_TITLE))
        allCourses.forEach {
            cursor.newRow()
                .add(it.id)
                .add(it.title)
        }
        return cursor
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        p1 ?: return null
        return when (uriMatcher.match(p0)) {
            TYPE_USERS -> saveUser(p1)
            TYPE_COURSES -> saveCourse(p1)
            else -> null
        }
    }

    private fun saveUser(values: ContentValues): Uri? {
        val userId = values.getAsLong(COLUMN_USER_ID) ?: return null
        val userName = values.getAsString(COLUMN_USER_NAME) ?: return null
        val userAge = values.getAsInteger(COLUMN_USER_AGE) ?: return null
        val user = User(userId, userName, userAge)
        userPrefs.edit()
            .putString(userId.toString(), userAdapter.toJson(user))
            .apply()
        return Uri.parse("content://$AUTHORITIES/$PATH_USERS/$userId")
    }

    private fun saveCourse(values: ContentValues): Uri {
        val courseId = values.getAsLong(COLUMN_COURSE_ID)
        val courseString = values.getAsString(COLUMN_COURSE_TITLE)
        val course = Course(courseId, courseString)
        coursesPrefs.edit()
            .putString(courseId.toString(), courseAdapter.toJson(course))
            .apply()
        return Uri.parse("content://$AUTHORITIES/$PATH_COURSES/$courseId")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return when (uriMatcher.match(p0)) {
            TYPE_USERS -> deleteUser(p0)
            TYPE_COURSES -> deleteCourse(p0)
            else -> 0
        }
    }

    private fun deleteUser(uri: Uri): Int {
        val userId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (userPrefs.contains(userId)) {
            userPrefs.edit()
                .remove(userId)
                .apply()
            1
        } else {
            0
        }
    }

    private fun deleteCourse(uri: Uri): Int {
        val courseId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (coursesPrefs.contains(courseId)) {
            coursesPrefs.edit()
                .remove(courseId)
                .apply()
            1
        } else {
            0
        }
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        p1 ?: return 0
        return when (uriMatcher.match(p0)) {
            TYPE_USERS -> updateUser(p0, p1)
            TYPE_COURSES -> updateCourse(p0, p1)
            else -> 0
        }
    }

    private fun updateUser(uri: Uri, contentValues: ContentValues): Int {
        val userId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (userPrefs.contains(userId)) {
            saveUser(contentValues)
            1
        } else {
            0
        }
    }

    private fun updateCourse(uri: Uri, contentValues: ContentValues): Int {
        val courseId = uri.lastPathSegment?.toLongOrNull()?.toString() ?: return 0
        return if (coursesPrefs.contains(courseId)) {
            saveCourse(contentValues)
            1
        } else {
            0
        }
    }

    companion object {
        private const val AUTHORITIES = "SkillBox App Provider"
        private const val PATH_USERS = "users"
        private const val PATH_COURSES = "courses"

        private const val TYPE_USERS = 1
        private const val TYPE_COURSES = 2
        private const val TYPE_USER_ID = 3
        private const val TYPE_COURSE_ID = 4

        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_AGE = "age"

        private const val COLUMN_COURSE_ID = "courseId"
        private const val COLUMN_COURSE_TITLE = "courseTitle"
    }
}