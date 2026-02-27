package com.thozhilpro.app.data.api

import com.thozhilpro.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("auth/firebase")
    suspend fun firebaseLogin(@Body request: FirebaseLoginRequest): Response<LoginResponse>

    @POST("auth/firebase/register")
    suspend fun firebaseRegister(@Body request: FirebaseLoginRequest): Response<LoginResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("auth/complete-profile")
    suspend fun completeProfile(@Body request: CompleteProfileRequest): Response<CompleteProfileResponse>

    @POST("auth/mark-verified")
    suspend fun markVerified(): Response<MarkVerifiedResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<CompleteProfileResponse>

    // Dashboard
    @GET("dashboard/stats")
    suspend fun getDashboardStats(): Response<DashboardStats>

    // Analytics
    @GET("analytics")
    suspend fun getAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<AnalyticsResponse>

    // Customers
    @GET("customers")
    suspend fun getCustomers(): Response<List<Customer>>

    @GET("customers/{id}")
    suspend fun getCustomer(@Path("id") id: Long): Response<Customer>

    @GET("customers/search")
    suspend fun searchCustomers(@Query("q") query: String): Response<List<Customer>>

    @POST("customers")
    suspend fun createCustomer(@Body request: CreateCustomerRequest): Response<Customer>

    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: Long, @Body request: CreateCustomerRequest): Response<Customer>

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: Long): Response<Unit>

    // Suppliers
    @GET("suppliers")
    suspend fun getSuppliers(): Response<List<Supplier>>

    @GET("suppliers/{id}")
    suspend fun getSupplier(@Path("id") id: Long): Response<Supplier>

    @GET("suppliers/search")
    suspend fun searchSuppliers(@Query("q") query: String): Response<List<Supplier>>

    @POST("suppliers")
    suspend fun createSupplier(@Body request: CreateSupplierRequest): Response<Supplier>

    @PUT("suppliers/{id}")
    suspend fun updateSupplier(@Path("id") id: Long, @Body request: CreateSupplierRequest): Response<Supplier>

    @DELETE("suppliers/{id}")
    suspend fun deleteSupplier(@Path("id") id: Long): Response<Unit>

    // Items
    @GET("items")
    suspend fun getItems(): Response<List<Item>>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: Long): Response<Item>

    @GET("items/search")
    suspend fun searchItems(@Query("q") query: String): Response<List<Item>>

    @POST("items")
    suspend fun createItem(@Body request: CreateItemRequest): Response<Item>

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") id: Long, @Body request: CreateItemRequest): Response<Item>

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: Long): Response<Unit>

    @POST("items/{id}/clear")
    suspend fun clearItemStock(@Path("id") id: Long): Response<Map<String, String>>

    // Purchases
    @GET("purchases")
    suspend fun getPurchases(): Response<List<Purchase>>

    @GET("purchases/{id}")
    suspend fun getPurchase(@Path("id") id: Long): Response<Purchase>

    @GET("purchases/range")
    suspend fun getPurchasesByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<Purchase>>

    @POST("purchases")
    suspend fun createPurchase(@Body request: CreatePurchaseRequest): Response<Purchase>

    @DELETE("purchases/{id}")
    suspend fun deletePurchase(@Path("id") id: Long): Response<Unit>

    // Sales (Wholesale)
    @GET("sales")
    suspend fun getSales(): Response<List<Sale>>

    @GET("sales/{id}")
    suspend fun getSale(@Path("id") id: Long): Response<Sale>

    @GET("sales/range")
    suspend fun getSalesByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<Sale>>

    @POST("sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Response<Sale>

    @DELETE("sales/{id}")
    suspend fun deleteSale(@Path("id") id: Long): Response<Unit>

    // Retail Sales
    @GET("retail-sales")
    suspend fun getRetailSales(): Response<List<RetailSale>>

    @GET("retail-sales/{id}")
    suspend fun getRetailSale(@Path("id") id: Long): Response<RetailSale>

    @GET("retail-sales/range")
    suspend fun getRetailSalesByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<RetailSale>>

    @POST("retail-sales")
    suspend fun createRetailSale(@Body request: CreateRetailSaleRequest): Response<RetailSale>

    @DELETE("retail-sales/{id}")
    suspend fun deleteRetailSale(@Path("id") id: Long): Response<Unit>

    // Payments
    @GET("payments")
    suspend fun getPayments(): Response<List<Payment>>

    @GET("payments/{id}")
    suspend fun getPayment(@Path("id") id: Long): Response<Payment>

    @GET("payments/range")
    suspend fun getPaymentsByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<Payment>>

    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<Payment>

    @PUT("payments/{id}")
    suspend fun updatePayment(@Path("id") id: Long, @Body request: CreatePaymentRequest): Response<Payment>

    @DELETE("payments/{id}")
    suspend fun deletePayment(@Path("id") id: Long): Response<Unit>

    // Expenses
    @GET("expenses")
    suspend fun getExpenses(): Response<List<Expense>>

    @GET("expenses/{id}")
    suspend fun getExpense(@Path("id") id: Long): Response<Expense>

    @GET("expenses/range")
    suspend fun getExpensesByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<Expense>>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<Expense>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: Long, @Body request: CreateExpenseRequest): Response<Expense>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit>

    // Notifications
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationMessage>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<Map<String, Int>>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): Response<Map<String, Boolean>>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<Map<String, Boolean>>

    @POST("notifications/push-token")
    suspend fun registerPushToken(@Body request: Map<String, String>): Response<Map<String, Boolean>>

    // Settings
    @GET("settings")
    suspend fun getSettings(): Response<Tenant>

    @PUT("settings")
    suspend fun updateSettings(@Body request: Map<String, String>): Response<Tenant>

    @POST("settings/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @GET("settings/users")
    suspend fun getUsers(): Response<List<Map<String, Any>>>

    // Subscription
    @GET("subscription/status")
    suspend fun getSubscriptionStatus(): Response<Map<String, Any>>

    @GET("subscription/plans")
    suspend fun getSubscriptionPlans(): Response<List<Map<String, Any>>>

    @POST("subscription/create-order")
    suspend fun createSubscriptionOrder(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<Map<String, Any>>

    @POST("subscription/verify-payment")
    suspend fun verifySubscriptionPayment(@Body request: @JvmSuppressWildcards Map<String, Any>): Response<Map<String, Any>>

    // Invoice PDF
    @GET("invoices/sale/{id}")
    suspend fun getSaleInvoicePdf(@Path("id") id: Long): Response<okhttp3.ResponseBody>

    @GET("invoices/retail/{id}")
    suspend fun getRetailSaleInvoicePdf(@Path("id") id: Long): Response<okhttp3.ResponseBody>
}
