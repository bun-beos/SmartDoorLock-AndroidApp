package vn.edu.hust.ttkien0311.smartlockdoor.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

private const val accounts_url = "/api/v1/Accounts"
private const val members_url = "/api/v1/Members"
private const val images_url = "/api/v1/Images"
private const val notifications_url = "/api/v1/Notifications"
private const val device_url = "/api/v1/Devices"

interface ServerApiService {
    // Account*********************************************
    // Đăng ký tài khoản
    @POST("${accounts_url}/Registration")
    suspend fun register(@Body accountDto: AccountDto): Int

    // Đăng nhập
    @POST("${accounts_url}/Login")
    suspend fun login(@Body accountDto: AccountDto): Token

    // Quên mật khẩu
    @POST("${accounts_url}/ForgotPassword")
    suspend fun forgotPassword(@Body email: String): Int

    // Đặt lại mật khẩu
    @POST("${accounts_url}/ResetPassword")
    suspend fun resetPassword(@Body passwordReset: PasswordReset): Int

    // Lấy thông tin tài khoản
    @GET("${accounts_url}/Info")
    suspend fun getAccountInfo(): Account

    // Làm mới access token
    @POST("${accounts_url}/NewAccessToken")
    fun refreshAccessToken(@Body refreshToken: String): Call<Token>

    // Đổi tên
    @PUT("${accounts_url}/Username")
    suspend fun changeUsername(@Query("name") username: String): Int

    // Đổi ảnh người dùng
    @PUT("${accounts_url}/UserImage")
    suspend fun changeUserImage(@Body imageData: String): Int

    // Đổi mật khẩu
    @PUT("${accounts_url}/Password")
    suspend fun changePassword(@Body passwordChange: PasswordChange): Int

    // Đăng xuất
    @POST("${accounts_url}/LogOut")
    suspend fun logOut(@Body refreshToken: String): Int

    // Xóa tài khoản
    @DELETE("${accounts_url}/Deletion")
    suspend fun deleteAccount(@Body password: String): Int
    //***********************************************************

    // Member****************************************************
    // Thêm mới thành viên
    @POST("${members_url}/NewMember")
    suspend fun createMember(@Body memberDto: MemberDto): Member?

    // Lấy danh sách thành viên
    @GET(members_url)
    suspend fun getAllByDeviceMember(@Query("deviceId") deviceId: String): List<Member>

    // Lấy thông tin thành viên theo id
    @GET("${members_url}/{id}")
    suspend fun getMember(@Path("id") id: String): Member

    // Thay đổi thông tin thành viên
    @PUT(members_url)
    suspend fun changeMemberInfo(
        @Query("memberId") memberId: String,
        @Body memberDto: MemberDto
    ): Member?

    // Xóa thành viên
    @DELETE("${members_url}/{id}")
    suspend fun deleteMember(@Path("id") id: String): Int

    // Xóa nhiều thành viên
    @DELETE(members_url)
    suspend fun deleteManyMembers(@Body listId: List<String>): Int
    //***********************************************************

    // Image*****************************************************
    // Lấy thời gian ảnh cũ nhất
    @GET("${images_url}/OldestTime/{deviceId}")
    suspend fun getOldestTime(@Path("deviceId") deviceId: String): String

    // Lấy ảnh theo thành viên hoặc thời gian
    @GET(images_url)
    suspend fun filterImage(
        @Query("deviceId") deviceId: String,
        @Query("memberId") memberId: String?,
        @Query("startDate") startDate: LocalDate?,
        @Query("endDate") endDate: LocalDate?
    ): List<Image>

    // Lấy ảnh theo id thông báo
    @GET("${images_url}/{notifId}")
    suspend fun getByNotifId(@Path("notifId") notifId: String): Image?

    // Xóa ảnh theo id
    @DELETE("${images_url}/{id}")
    suspend fun deleteImage(@Path("id") id: String): Int

    // Xóa nhiều ảnh
    @DELETE(images_url)
    suspend fun deleteManyImages(@Body listId: List<String>): Int
    //***********************************************************

    // Notification**********************************************
    // Lấy danh sách thông báo
    @GET("${notifications_url}/Filter")
    suspend fun FilterNotification(
        @Query("accountId") accountId: String,
        @Query("deviceId") deviceId: String? = null
    ): List<Notification>

    // Cập nhập trạng thái thông báo
    @PUT("${notifications_url}/{id}")
    suspend fun updateNotification(@Path("id") id: String): Int

    // Cập nhập trạng thái nhiều thông báo
    @PUT(notifications_url)
    suspend fun updateNotification(@Body listId: List<String>): Int

    // Xóa 1 thông báo
    @DELETE("${notifications_url}/{id}")
    suspend fun deleteNotification(@Path("id") id: String): Int

    // Xóa nhiều thông báo
    @DELETE(notifications_url)
    suspend fun deleteManyNotifications(@Body listId: List<String>): Int
    //***********************************************************

    // Device******************************************************
    // Lấy tất cả thông tin thiết bị
    @GET(device_url)
    suspend fun getAllDevice(@Query("accountId") accountId: String): List<Device>

    // Lấy thông tin thiết bị theo accountId
    @GET("${device_url}/{accountId}")
    suspend fun getDeviceByAccount(@Path("accountId") accountId: String): List<Device>

    // Cập nhập thông tin thiết bị
    @PUT(device_url)
    suspend fun updateDevice(@Body device: Device): Int

    // Xóa thông tin thiết bị
    @DELETE("${device_url}/{deviceId}")
    suspend fun deleteDevice(@Path("deviceId") id: String): Int
}