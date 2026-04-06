package com.purina.feedright.config

import com.purina.feedright.model.Farm
import com.purina.feedright.model.Product
import com.purina.feedright.model.Salesman
import com.purina.feedright.repository.FarmRepository
import com.purina.feedright.repository.ProductRepository
import com.purina.feedright.repository.SalesmanRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataInitializer {

    @Bean
    fun initData(
        salesmanRepository: SalesmanRepository,
        farmRepository: FarmRepository,
        productRepository: ProductRepository,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        // Only seed if database is empty
        if (salesmanRepository.count() > 0) {
            println("Database already has data, skipping initialization")
            return@CommandLineRunner
        }

        println("Initializing database with test data...")

        // Create salesmen (PIN: 1234 for all, for MVP testing)
        val salesmen = listOf(
            Salesman(
                name = "Marco Silva",
                phone = "+1234567890",
                territory = "North District",
                pin = passwordEncoder.encode("1234")
            ),
            Salesman(
                name = "Ana Costa",
                phone = "+1234567891",
                territory = "North District",
                pin = passwordEncoder.encode("1234")
            ),
            Salesman(
                name = "Carlos Santos",
                phone = "+1234567892",
                territory = "South District",
                pin = passwordEncoder.encode("1234")
            ),
            Salesman(
                name = "Maria Oliveira",
                phone = "+1234567893",
                territory = "East District",
                pin = passwordEncoder.encode("1234")
            ),
            Salesman(
                name = "João Pereira",
                phone = "+1234567894",
                territory = "North District",
                pin = passwordEncoder.encode("1234")
            )
        )
        salesmanRepository.saveAll(salesmen)
        println("Created ${salesmen.size} salesmen")

        // Create farms
        val farms = listOf(
            Farm(name = "Green Valley Farm", location = "Green Valley, North District", territory = "North District"),
            Farm(name = "Sunrise Pig Farm", location = "Sunrise Hills, North District", territory = "North District"),
            Farm(name = "Mountain View Ranch", location = "Mountain View, North District", territory = "North District"),
            Farm(name = "River Side Farm", location = "River Road, North District", territory = "North District"),
            Farm(name = "Oak Tree Farm", location = "Oak Street, North District", territory = "North District"),
            Farm(name = "Meadow Farm", location = "Meadow Lane, South District", territory = "South District"),
            Farm(name = "Valley View Farm", location = "Valley Road, South District", territory = "South District"),
            Farm(name = "Sunset Farm", location = "Sunset Ave, South District", territory = "South District"),
            Farm(name = "Hillside Ranch", location = "Hill Road, East District", territory = "East District"),
            Farm(name = "Prairie Farm", location = "Prairie Street, East District", territory = "East District"),
            Farm(name = "Golden Acres", location = "Golden Road, North District", territory = "North District"),
            Farm(name = "Silver Springs Farm", location = "Silver Springs, North District", territory = "North District"),
            Farm(name = "Blue Sky Ranch", location = "Blue Sky Road, South District", territory = "South District"),
            Farm(name = "Red Barn Farm", location = "Red Barn Lane, East District", territory = "East District"),
            Farm(name = "White Oak Farm", location = "White Oak Street, North District", territory = "North District"),
            Farm(name = "Black Creek Farm", location = "Black Creek Road, South District", territory = "South District"),
            Farm(name = "Pine Hill Farm", location = "Pine Hill Ave, East District", territory = "East District"),
            Farm(name = "Maple Grove Farm", location = "Maple Grove, North District", territory = "North District"),
            Farm(name = "Cedar Ridge Farm", location = "Cedar Ridge Road, South District", territory = "South District"),
            Farm(name = "Willow Creek Ranch", location = "Willow Creek, East District", territory = "East District")
        )
        farmRepository.saveAll(farms)
        println("Created ${farms.size} farms")

        // Create pig products (Purina catalogue)
        val products = listOf(
            Product(sku = "PPG-16", name = "Purina Pro Pig Grower 16%", category = "pig", isActive = true),
            Product(sku = "PPG-18", name = "Purina Pro Pig Grower 18%", category = "pig", isActive = true),
            Product(sku = "PPS-14", name = "Purina Pig Starter 14%", category = "pig", isActive = true),
            Product(sku = "PPS-16", name = "Purina Pig Starter 16%", category = "pig", isActive = true),
            Product(sku = "PPF-15", name = "Purina Pig Finisher 15%", category = "pig", isActive = true),
            Product(sku = "PPF-17", name = "Purina Pig Finisher 17%", category = "pig", isActive = true),
            Product(sku = "PPN-12", name = "Purina Pig Nursery 12%", category = "pig", isActive = true),
            Product(sku = "PPB-16", name = "Purina Pig Breeder 16%", category = "pig", isActive = true),
            Product(sku = "PPB-18", name = "Purina Pig Breeder 18%", category = "pig", isActive = true),
            Product(sku = "PPR-20", name = "Purina Pig Recovery Formula 20%", category = "pig", isActive = true),
            Product(sku = "PPC-14", name = "Purina Pig Creep Feed 14%", category = "pig", isActive = true),
            Product(sku = "PPC-16", name = "Purina Pig Creep Feed 16%", category = "pig", isActive = true),
            Product(sku = "PPW-13", name = "Purina Pig Weaner 13%", category = "pig", isActive = true),
            Product(sku = "PPW-15", name = "Purina Pig Weaner 15%", category = "pig", isActive = true),
            Product(sku = "PPM-16", name = "Purina Pig Maintenance 16%", category = "pig", isActive = true),
            Product(sku = "PPO-14", name = "Purina Pig Organic Grower 14%", category = "pig", isActive = true),
            Product(sku = "PPO-16", name = "Purina Pig Organic Grower 16%", category = "pig", isActive = true),
            Product(sku = "PPE-18", name = "Purina Pig Elite Performance 18%", category = "pig", isActive = true),
            Product(sku = "PPE-20", name = "Purina Pig Elite Performance 20%", category = "pig", isActive = true),
            Product(sku = "PPP-16", name = "Purina Pig Premium Mix 16%", category = "pig", isActive = true),

            // Add a few cattle products for future expansion
            Product(sku = "PCD-16", name = "Purina Cattle Dairy 16%", category = "cattle", isActive = false),
            Product(sku = "PCB-14", name = "Purina Cattle Beef 14%", category = "cattle", isActive = false),
            Product(sku = "PCC-18", name = "Purina Cattle Calf Starter 18%", category = "cattle", isActive = false)
        )
        productRepository.saveAll(products)
        println("Created ${products.size} products (${products.count { it.category == "pig" && it.isActive }} active pig products)")

        println("Database initialization complete!")
        println("Test credentials: Phone: +1234567890, PIN: 1234")
    }
}
