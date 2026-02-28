package com.thozhilpro.app.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

// ===== AUTH =====
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String)
data class FirebaseLoginRequest(val idToken: String)
data class RefreshTokenRequest(val refreshToken: String)
data class CompleteProfileRequest(
    val firstName: String,
    val lastName: String,
    val companyName: String,
    val phone: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: User,
    val tenant: Tenant
)

data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String
)

data class CompleteProfileResponse(
    val user: User,
    val tenant: Tenant
)

data class MarkVerifiedResponse(
    val user: User
)

// ===== USER =====
data class User(
    val id: Long,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: String?,
    val enabled: Boolean = true,
    val profileCompleted: Boolean = false,
    val verified: Boolean = false,
    val authProvider: String? = "email"
)

// ===== TENANT =====
data class Tenant(
    val id: Long,
    val subdomain: String?,
    val companyName: String?,
    val logo: String?,
    val primaryColor: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val currency: String? = "INR",
    val plan: String? = "FREE",
    var status: String? = "ACTIVE",
    val maxUsers: Int? = 1,
    val maxItems: Int? = 50,
    val trialEndsAt: String?,
    var subscriptionEndsAt: String?,
    val gstEnabled: Boolean? = false,
    val gstNumber: String?,
    val gstPercentage: BigDecimal? = BigDecimal("18.00"),
    val paymentDelayAlertDays: Int? = 10,
    val stockAgeAlertDays: Int? = 10
)

// ===== CUSTOMER =====
data class Customer(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String,
    val address: String?,
    val idType: String?,
    val idNumber: String?,
    val previousBalance: BigDecimal? = BigDecimal.ZERO,
    val pendingAmount: BigDecimal? = BigDecimal.ZERO,
    val totalSales: BigDecimal? = BigDecimal.ZERO,
    val totalPayments: BigDecimal? = BigDecimal.ZERO
) : java.io.Serializable {
    val fullName: String get() = "$firstName $lastName"
    val balance: BigDecimal get() = (pendingAmount ?: BigDecimal.ZERO).add(previousBalance ?: BigDecimal.ZERO)
}

data class CreateCustomerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val previousBalance: BigDecimal? = BigDecimal.ZERO
)

data class UpdateCustomerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val idType: String? = null,
    val idNumber: String? = null
)

data class CustomerDetail(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val idType: String?,
    val idNumber: String?,
    val previousBalance: BigDecimal = BigDecimal.ZERO,
    val totalPending: BigDecimal = BigDecimal.ZERO,
    val totalSales: BigDecimal = BigDecimal.ZERO,
    val bills: List<SaleBill> = emptyList(),
    val hasDelayedPayments: Boolean = false,
    val paymentDelayAlertDays: Int = 10
) {
    val fullName: String get() = "$firstName $lastName"
    val totalDue: BigDecimal get() = totalPending.add(previousBalance)
}

data class SaleBill(
    val id: Long,
    val invoiceNumber: String?,
    val saleDate: String?,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val paymentReceived: BigDecimal = BigDecimal.ZERO,
    val balanceDue: BigDecimal = BigDecimal.ZERO,
    val status: String?,
    val paymentDelayed: Boolean = false,
    val daysOverdue: Long = 0,
    val lineItems: List<SaleBillLineItem> = emptyList()
)

data class SaleBillLineItem(
    val itemName: String?,
    val quantity: Int = 0,
    val rate: BigDecimal = BigDecimal.ZERO,
    val lineTotal: BigDecimal = BigDecimal.ZERO
)

data class SettlementRequest(
    val customerId: Long,
    val saleIds: List<Long> = emptyList(),
    val paymentAmount: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val paymentMode: String = "CASH",
    val description: String? = null,
    val paymentDate: String? = null,
    val againstPreviousBalance: Boolean = false
)

data class SettlementResponse(
    val customerId: Long?,
    val customerName: String?,
    val settledSaleIds: List<Long>? = emptyList(),
    val totalBillAmount: BigDecimal? = BigDecimal.ZERO,
    val discountApplied: BigDecimal? = BigDecimal.ZERO,
    val paymentReceived: BigDecimal? = BigDecimal.ZERO,
    val remainingBalance: BigDecimal? = BigDecimal.ZERO,
    val previousBalance: BigDecimal? = BigDecimal.ZERO,
    val paymentId: Long?,
    val message: String?,
    val error: String? = null
)

// ===== SUPPLIER =====
data class Supplier(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val email: String?,
    val phone: String,
    val address: String?,
    val gstNumber: String?,
    val previousBalance: BigDecimal? = BigDecimal.ZERO,
    val notes: String?,
    val pendingAmount: BigDecimal? = BigDecimal.ZERO,
    val totalPurchases: BigDecimal? = BigDecimal.ZERO,
    val totalPayments: BigDecimal? = BigDecimal.ZERO
) : java.io.Serializable {
    val balance: BigDecimal get() = (pendingAmount ?: BigDecimal.ZERO).add(previousBalance ?: BigDecimal.ZERO)
}

data class CreateSupplierRequest(
    val name: String,
    val contactPerson: String? = null,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val previousBalance: BigDecimal? = BigDecimal.ZERO
)

// ===== ITEM =====
data class Item(
    val id: Long,
    val name: String,
    val description: String?,
    val category: String?,
    val unit: String?,
    val quantity: Int = 0,
    val purchaseRate: BigDecimal? = BigDecimal.ZERO,
    val internalRate: BigDecimal? = BigDecimal.ZERO,
    val wholesalePrice: BigDecimal? = BigDecimal.ZERO,
    val retailPrice: BigDecimal? = BigDecimal.ZERO,
    val investmentAmount: BigDecimal? = BigDecimal.ZERO,
    val location: String?,
    val expiryDate: String?,
    val imageUrl: String?,
    val sku: String?,
    val hsnCode: String?,
    val notes: String?,
    val status: String? = "ACTIVE"
) : java.io.Serializable

data class CreateItemRequest(
    val name: String,
    val category: String? = null,
    val unit: String? = null,
    val quantity: Int = 0,
    val purchaseRate: BigDecimal? = BigDecimal.ZERO,
    val wholesalePrice: BigDecimal? = BigDecimal.ZERO,
    val retailPrice: BigDecimal? = BigDecimal.ZERO,
    val location: String? = null,
    val description: String? = null
)

// ===== PURCHASE =====
data class Purchase(
    val id: Long,
    val supplier: Supplier?,
    val purchaseDate: String,
    val invoiceNumber: String?,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val handlingCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val totalAmount: BigDecimal? = BigDecimal.ZERO,
    val billImageUrl: String?,
    val notes: String?,
    val status: String?,
    val lineItems: List<PurchaseLineItem>? = emptyList()
) : java.io.Serializable

data class PurchaseLineItem(
    val id: Long? = null,
    val itemId: Long? = null,
    val itemName: String,
    val quantity: Int,
    val rate: BigDecimal,
    val lineTotal: BigDecimal? = null
) : java.io.Serializable

data class CreatePurchaseRequest(
    val supplierId: Long,
    val purchaseDate: String,
    val invoiceNumber: String? = null,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val handlingCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val notes: String? = null,
    val lineItems: List<PurchaseLineItem> = emptyList()
)

// ===== SALE =====
data class Sale(
    val id: Long,
    val customer: Customer?,
    val saleDate: String,
    val invoiceNumber: String?,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val handlingCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val discountAmount: BigDecimal? = BigDecimal.ZERO,
    val totalAmount: BigDecimal? = BigDecimal.ZERO,
    val paymentReceived: BigDecimal? = BigDecimal.ZERO,
    val notes: String?,
    val status: String?,
    val lineItems: List<SaleLineItem>? = emptyList()
) : java.io.Serializable

data class SaleLineItem(
    val id: Long? = null,
    val itemId: Long? = null,
    val itemName: String,
    val quantity: Int,
    val rate: BigDecimal,
    val lineTotal: BigDecimal? = null
) : java.io.Serializable

data class CreateSaleRequest(
    val customerId: Long,
    val saleDate: String,
    val invoiceNumber: String? = null,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val handlingCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val discountAmount: BigDecimal? = BigDecimal.ZERO,
    val paymentReceived: BigDecimal? = BigDecimal.ZERO,
    val notes: String? = null,
    val lineItems: List<SaleLineItem> = emptyList()
)

// ===== RETAIL SALE =====
data class RetailSale(
    val id: Long,
    val customerName: String?,
    val customerPhone: String?,
    val saleDate: String,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val discountAmount: BigDecimal? = BigDecimal.ZERO,
    val totalAmount: BigDecimal? = BigDecimal.ZERO,
    val paymentMode: String? = "CASH",
    val paymentReceived: BigDecimal? = BigDecimal.ZERO,
    val notes: String?,
    val lineItems: List<RetailSaleLineItem>? = emptyList()
) : java.io.Serializable

data class RetailSaleLineItem(
    val id: Long? = null,
    val itemId: Long? = null,
    val itemName: String,
    val quantity: Int,
    val rate: BigDecimal,
    val lineTotal: BigDecimal? = null
) : java.io.Serializable

data class CreateRetailSaleRequest(
    val customerName: String? = null,
    val customerPhone: String? = null,
    val saleDate: String,
    val transportCharges: BigDecimal? = BigDecimal.ZERO,
    val otherCharges: BigDecimal? = BigDecimal.ZERO,
    val discountAmount: BigDecimal? = BigDecimal.ZERO,
    val paymentReceived: BigDecimal? = BigDecimal.ZERO,
    val paymentMode: String? = "CASH",
    val notes: String? = null,
    val lineItems: List<RetailSaleLineItem> = emptyList()
)

// ===== PAYMENT =====
data class Payment(
    val id: Long,
    val customer: Customer?,
    val supplier: Supplier?,
    val amount: BigDecimal,
    val paymentMode: String? = "CASH",
    val paymentType: String? = "RECEIVED",
    val paymentDate: String,
    val description: String?
) : java.io.Serializable, Hashable

data class CreatePaymentRequest(
    val customerId: Long? = null,
    val supplierId: Long? = null,
    val saleId: Long? = null,
    val purchaseId: Long? = null,
    val amount: BigDecimal,
    val paymentMode: String = "CASH",
    val paymentType: String = "RECEIVED",
    val paymentDate: String,
    val description: String? = null
)

// ===== EXPENSE =====
data class Expense(
    val id: Long,
    val type: String,
    val amount: BigDecimal,
    val description: String?,
    val expenseDate: String
) : java.io.Serializable, Hashable

data class CreateExpenseRequest(
    val type: String,
    val amount: BigDecimal,
    val description: String? = null,
    val expenseDate: String
)

// ===== DASHBOARD =====
data class DashboardStats(
    val totalItems: Int? = 0,
    val inStockItems: Int? = 0,
    val outOfStockItems: Int? = 0,
    val totalCustomers: Int? = 0,
    val totalSuppliers: Int? = 0,
    val totalPurchases: Int? = 0,
    val totalSales: Int? = 0,
    val todaySales: Int? = 0,
    val todayPayments: BigDecimal? = BigDecimal.ZERO,
    val totalInvestment: BigDecimal? = BigDecimal.ZERO,
    val monthlyRevenue: BigDecimal? = BigDecimal.ZERO,
    val lastMonthRevenue: BigDecimal? = BigDecimal.ZERO,
    val totalPendingFromCustomers: BigDecimal? = BigDecimal.ZERO,
    val totalPendingToSuppliers: BigDecimal? = BigDecimal.ZERO,
    val customersWithDues: Int? = 0,
    val weeklyRevenue: List<WeeklyRevenueEntry>? = emptyList(),
    val topCustomersByBalance: List<TopCustomerEntry>? = emptyList()
)

data class WeeklyRevenueEntry(val day: String?, val revenue: BigDecimal?)
data class TopCustomerEntry(val name: String?, val balance: BigDecimal?)

// ===== ANALYTICS =====
data class AnalyticsResponse(
    val totalWholesaleSales: BigDecimal? = BigDecimal.ZERO,
    val totalRetailSales: BigDecimal? = BigDecimal.ZERO,
    val totalPurchases: BigDecimal? = BigDecimal.ZERO,
    val totalExpenses: BigDecimal? = BigDecimal.ZERO,
    val totalReceived: BigDecimal? = BigDecimal.ZERO,
    val totalPaid: BigDecimal? = BigDecimal.ZERO,
    val profitLoss: BigDecimal? = BigDecimal.ZERO,
    val revenueByDay: List<RevenueDayEntry>? = emptyList(),
    val expenseBreakdown: Map<String, BigDecimal>? = emptyMap(),
    val fastMovingItems: List<ItemMovementEntry>? = emptyList(),
    val notMovingItems: List<ItemMovementEntry>? = emptyList()
)

data class RevenueDayEntry(val date: String?, val revenue: BigDecimal?)
data class ItemMovementEntry(
    val name: String?,
    val quantity: Int? = 0,
    val quantitySold: Int? = 0,
    val daysInStock: Int? = 0
)

// ===== NOTIFICATION =====
data class NotificationMessage(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val message: String?,
    val type: String?,
    @SerializedName("read") val isRead: Boolean = false,
    val createdAt: String?
)

// Hashable marker interface for .sheet(item:) support
interface Hashable
