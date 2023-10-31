import java.io.File
import java.io.IOException
import java.util.Scanner
import java.util.Date

interface BillEnhancer {
    fun applyEnhancement(finalBill: Double): Double
}

data class User(val name: String, val address: String, val userType: String)

data class MenuItem(val name: String, val price: Double)

class DiscountEnhancer(private val discountPercentage: Double) : BillEnhancer {
    override fun applyEnhancement(finalBill: Double): Double {
        val discountAmount = finalBill * discountPercentage
        return finalBill - discountAmount
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val currentTime = Date()

    println("Welcome to Tomatino's")

    print("Enter your name: ")
    val name = scanner.nextLine()

    print("Enter your address: ")
    val address = scanner.nextLine()

    var userType: String = ""

    try {
        print("Are you a guest, regular, or premium user? (guest/regular/premium): ")
        userType = scanner.nextLine()
        if (userType != "guest" && userType != "regular" && userType != "premium") {
            throw IllegalArgumentException("Invalid user type. Please select 'guest', 'regular', or 'premium'.")
        }
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    val menu = mapOf(
        "Pizza" to MenuItem("Pizza", 10.0),
        "Burger" to MenuItem("Burger", 5.0),
        "Pasta" to MenuItem("Pasta", 8.0)
    )

    println("Menu:")
    menu.values.forEach { item ->
        println("${item.name} - ${item.price} USD")
    }

    val order = mutableMapOf<MenuItem, Int>()
    var totalBill = 0.0

    while (true) {
        print("Enter the food item you want to order (or 'done' to finish): ")
        val foodItem = scanner.nextLine()

        if (foodItem == "done") {
            break
        }

        val menuItem = menu[foodItem]

        if (menuItem == null) {
            println("Invalid food item. Please choose from the menu.")
            continue
        }

        try {
            print("Enter the quantity: ")
            val quantity = scanner.nextLine().toInt()

            if (quantity < 0) {
                throw IllegalArgumentException("Quantity must be a positive number.")
            }

            order[menuItem] = order.getOrDefault(menuItem, 0) + quantity
            totalBill += menuItem.price * quantity
        } catch (e: NumberFormatException) {
            println("Invalid input. Please enter a valid quantity.")
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }

    val discountPercentage = when (userType) {
        "guest" -> 0.10
        "regular" -> 0.20
        "premium" -> 0.30
        else -> 0.0
    }

    val discountEnhancer = DiscountEnhancer(discountPercentage)
    val enhancedFinalBill = discountEnhancer.applyEnhancement(totalBill)

    println("\nOrder Summary:")
    println("Name: $name")
    println("Address: $address")
    println("User Type: $userType")
    println("Food Ordered:")
    order.forEach { (item, quantity) ->
        println("${item.name} - $quantity")
    }
    println("Total Bill: $totalBill USD")
    println("Discount: ${discountPercentage * 100}%")
    println("Discount Amount: ${totalBill - enhancedFinalBill} USD")
    println("Final Bill: $enhancedFinalBill USD")
    println("Current Time: $currentTime")
    
    val orderDetails = """
        |Order Summary:
        |Name: $name
        |Address: $address
        |User Type: $userType
        |Food Ordered:
        |${order.map { "${it.key.name} - ${it.value}" }.joinToString("\n")}
        |Total Bill: $totalBill USD
        |Discount: ${discountPercentage * 100}%
        |Discount Amount: ${totalBill - enhancedFinalBill} USD
        |Final Bill: $enhancedFinalBill USD
        |Current Time: $currentTime
    """.trimMargin()

    val fileName = "order_${userType}.txt"
    try {
        File(fileName).writeText(orderDetails)
        println("Order details saved to $fileName")
    } catch (e: IOException) {
        println("Error saving order details: ${e.message}")
    }
}
