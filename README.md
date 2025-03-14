🛠️ Real-Time Stock Trading Engine
A high-performance stock trading engine that efficiently matches Buy & Sell orders using lock-free concurrent Skip Lists. Designed for multi-threaded execution, this engine ensures real-time stock order matching while avoiding race conditions.

📌 Features
✅ Supports 1,024+ stock tickers (AAPL, GOOGL, AMZN, etc.).
✅ Real-time order matching with O(n) time complexity.
✅ Thread-safe, lock-free execution using AtomicMarkableReference.
✅ No maps or dictionaries – fully compliant with task constraints.
✅ Simulates multi-threaded stock transactions using ExecutorService.
✅ ANSI color-coded terminal output for enhanced readability.

📜 How It Works
1️⃣ The system randomly generates stock orders (Buy/Sell).
2️⃣ Orders are stored in a lock-free Skip List per ticker symbol.
3️⃣ The matching engine runs in O(n) time, executing trades where buyPrice ≥ sellPrice.
4️⃣ The final order book is displayed with all unmatched Buy/Sell orders.

---

## 📌 **Features**
✅ Supports **1,024 stock tickers** (AAPL, GOOGL, AMZN, MSFT, TSLA, etc.).  
✅ Handles **multi-threaded** concurrent order execution.  
✅ Uses **lock-free Skip Lists** for **O(log n) order insertion & retrieval**.  
✅ Implements **real-time order matching** with **O(n) complexity**.  
✅ Uses **ANSI-colored console output** for better readability.  

---

## ⚙️ **Tech Stack**
- **Java 17+**
- **Concurrent Data Structures (AtomicMarkableReference)**
- **ANSI Color Codes (for better console output)**
- **IntelliJ IDEA (Recommended IDE)**

---
##  Run the Project
javac -d out src/com/tradingengine/*.java
java -cp out com.tradingengine.TradingSimulation
