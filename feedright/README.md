# FeedRight - Field Sales Visit Tracking System

A complete visit recording system for Purina salesmen to track farm visits, product recommendations, and field activity.

## Project Overview

FeedRight is a **3-tier system** designed to solve the problem of salesmen relying on memory for product recommendations and having no visit tracking data.

### System Architecture

```
┌─────────────────────────┐
│   Android App (TBD)     │  ← Field salesmen use this
│   - Offline-first       │
│   - Visit recording     │
│   - SQLite + WorkMgr    │
└──────────┬──────────────┘
           │
           │ HTTPS/REST
           │
┌──────────▼──────────────┐
│   Spring Boot API       │  ✅ COMPLETE & TESTED
│   - Kotlin              │
│   - PostgreSQL          │
│   - Batch sync          │
└──────────┬──────────────┘
           │
           │ REST API
           │
┌──────────▼──────────────┐
│   React Dashboard       │  ✅ COMPLETE (needs Node.js upgrade)
│   - Visit feed          │
│   - Filters             │
│   - CSV export          │
└─────────────────────────┘
```

## Project Structure

```
feedright/
├── backend/              ✅ Spring Boot (Kotlin) + PostgreSQL
├── dashboard/            ✅ React + Vite + Tailwind
├── android-app/          📋 Not started (planned: Kotlin + Jetpack Compose)
├── docker-compose.yml    ✅ PostgreSQL setup
└── README.md            ← You are here
```

---

## 🎯 What's Been Built

### ✅ Backend API (Spring Boot + Kotlin)

**Status:** Complete, tested, and running

**Features:**
- RESTful API for visits, farms, products, salesmen
- Batch sync endpoint for offline visit uploads
- Idempotency support (prevents duplicate syncs)
- PIN-based authentication
- Auto-seeded test data
- Offline sync support

**Tech Stack:**
- Kotlin
- Spring Boot 3.2.4
- PostgreSQL 14
- Spring Data JPA
- Gradle

**Running:**
```bash
# Start PostgreSQL
cd feedright
docker-compose up -d

# Run backend
cd backend
./gradlew bootRun
```

Backend runs on `http://localhost:8080`

**Test Data:**
- 5 salesmen (Marco, Ana, Carlos, Maria, João)
- 20 farms (North, South, East Districts)
- 23 products (20 active pig products)
- Test credentials: Phone `+1234567890`, PIN `1234`

**Endpoints:**
- `POST /api/auth/login` - Authentication
- `GET /api/visits` - All visits
- `POST /api/visits` - Create visit
- `POST /api/visits/sync` - Batch sync
- `GET /api/farms` - All farms
- `GET /api/products` - All products
- `GET /api/salesmen` - All salesmen

See `backend/README.md` for full API documentation.

---

### ✅ React Dashboard

**Status:** Complete (requires Node.js v14.18+)

**Features:**
- Real-time visit feed from backend
- Smart filters (salesman, farm, date range)
- Summary metrics dashboard
- CSV export functionality
- Responsive design (mobile-friendly)
- Loading states and error handling

**Tech Stack:**
- React 18 + TypeScript
- Vite (fast build tool)
- Tailwind CSS
- Axios (API client)
- date-fns

**Running:**

⚠️ **Requires Node.js v14.18+ (current: v13.8.0)**

```bash
# Upgrade Node first (see dashboard/README.md)

# Then run:
cd dashboard
npm install
npm run dev
```

Dashboard runs on `http://localhost:3000`

See `dashboard/README.md` for full setup instructions.

---

### 📋 Android App (Planned)

**Status:** Not started

**Planned Tech Stack:**
- Kotlin
- Jetpack Compose (UI)
- Room (offline SQLite database)
- Retrofit (API client)
- WorkManager (background sync)

**Planned Features:**
- Offline-first visit recording
- Farm/product dropdowns
- Background sync queue
- Visit history (last 10)
- PIN authentication

---

## 🗄️ Database Schema

### Entities

**Salesman**
- id (UUID)
- name, phone, territory
- pin (BCrypt hashed)

**Farm**
- id (UUID)
- name, location, territory

**Product**
- id (UUID)
- sku, name, category
- is_active

**Visit** (Core entity)
- id (UUID)
- salesman_id, farm_id, product_id (foreign keys)
- quantity, visit_date, notes
- device_id, created_at, synced_at
- Indexed on: visit_date, (device_id + created_at)

---

## 🚀 Quick Start (Backend + Dashboard)

### Prerequisites

- Java 17+
- Docker Desktop
- Node.js v14.18+ (for dashboard)

### Step 1: Start Backend

```bash
# Terminal 1: Start PostgreSQL
cd feedright
docker-compose up -d

# Build and run backend
cd backend
./gradlew clean build
./gradlew bootRun
```

Backend starts on http://localhost:8080

### Step 2: Test Backend

```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+1234567890","pin":"1234"}'

# Get all visits
curl http://localhost:8080/api/visits
```

### Step 3: Start Dashboard

```bash
# Terminal 2: Install dependencies
cd dashboard
npm install

# Start dev server
npm run dev
```

Dashboard opens at http://localhost:3000

---

## 📊 System Status

| Component | Status | Port | Notes |
|-----------|--------|------|-------|
| PostgreSQL | ✅ Running | 5432 | Via Docker |
| Backend API | ✅ Running | 8080 | Spring Boot |
| React Dashboard | ⚠️ Ready (needs Node upgrade) | 3000 | Vite dev server |
| Android App | 📋 Not started | - | Planned |

---

## 🧪 Testing Results

### Backend Tests (All Passed ✅)

1. ✅ Authentication endpoint
2. ✅ Get all products (20 active pig products)
3. ✅ Get all farms (20 farms)
4. ✅ Get all salesmen (5 salesmen)
5. ✅ Create visit
6. ✅ Batch sync (2 visits)
7. ✅ Idempotency (duplicate sync prevention)
8. ✅ Data seeding

### Dashboard (Ready to Test)

Once Node.js is upgraded:
- [ ] Load visit feed from backend
- [ ] Apply filters (salesman, farm, date range)
- [ ] Display summary metrics
- [ ] Export to CSV

---

## 📝 Implementation Timeline

**Phase 1: Backend (Completed ✅)**
- [x] Set up project structure
- [x] Create Spring Boot skeleton
- [x] Define JPA entities
- [x] Implement repositories
- [x] Build service layer
- [x] Create REST controllers
- [x] Configure security
- [x] Add data seeding
- [x] Test all endpoints

**Phase 2: Dashboard (Completed ✅)**
- [x] Set up React + Vite + TypeScript
- [x] Configure Tailwind CSS
- [x] Create API client
- [x] Build StatsCard component
- [x] Build VisitTable component
- [x] Build Filters component
- [x] Implement Dashboard page
- [x] Add CSV export
- [ ] Test with backend (pending Node upgrade)

**Phase 3: Android App (Not Started 📋)**
- [ ] Set up Android project
- [ ] Implement Room database
- [ ] Build Jetpack Compose UI
- [ ] Integrate Retrofit API client
- [ ] Implement WorkManager sync
- [ ] Add offline queue
- [ ] Test end-to-end

---

## 🎯 MVP Scope (Refined from Idea-Refine Process)

### What's In
✅ Visit recording (salesman, farm, product, quantity, notes)
✅ Offline-first architecture
✅ Batch sync with idempotency
✅ Manager dashboard with filters
✅ CSV export
✅ Summary metrics

### What's Out (Deferred to Phase 2+)
❌ Recommendation engine (scoring logic)
❌ Order capture/ERP integration
❌ Animal group data (age/weight/health)
❌ GPS tracking
❌ Push notifications
❌ Multi-language support

---

## 🔧 Development Commands

### Backend

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies
```

### Dashboard

```bash
# Install deps
npm install

# Dev server
npm run dev

# Build for prod
npm run build

# Preview prod build
npm run preview
```

### Docker

```bash
# Start PostgreSQL
docker-compose up -d

# Stop
docker-compose down

# View logs
docker-compose logs -f postgres

# Reset data
docker-compose down -v
```

---

## 🐛 Known Issues

1. **Dashboard requires Node.js upgrade**
   - Current: v13.8.0
   - Required: v14.18.0+
   - Solution: See `dashboard/README.md`

2. **JWT authentication is simplified**
   - Using `Bearer-{salesmanId}` instead of signed JWT
   - Acceptable for MVP, should be hardened for production

3. **No input validation yet**
   - API accepts any data
   - Should add Bean Validation in production

---

## 🎓 Lessons from Idea-Refine Process

The original vision included a **recommendation engine** (scoring logic for products based on age/weight/health). Through the idea-refine process, we decided to:

1. **Start smaller** - Build visit recording first, recommendation engine second
2. **Validate the problem** - The "wrong recommendations" issue is anecdotal, not proven
3. **De-risk adoption** - A 15-second post-visit log is more likely to stick than a 60-second mid-visit form
4. **Let data guide decisions** - After 8 weeks of visit data, we'll know if the recommendation engine is needed

This approach reduces scope by ~40% while preserving the core value: **visit data visibility.**

---

## 📚 Documentation

- `backend/README.md` - Backend setup, API docs, testing
- `dashboard/README.md` - Dashboard setup, features, troubleshooting
- `docs/ideas/purina-feedright-product-idea.md` - Original product vision
- `docs/ideas/purina-feedright-mvp-refined.md` - Refined MVP plan

---

## 🚢 Next Steps

1. **Upgrade Node.js** to v14.18+ (to run dashboard)
2. **Test dashboard** end-to-end with backend
3. **Build Android app** (Kotlin + Jetpack Compose)
4. **Pilot deployment** (8-week test with 5-8 salesmen)
5. **Collect visit data** and decide on recommendation engine

---

## 📧 Project Context

This project was built following the **agent-skills** methodology:
- `/ideate` - Used idea-refine skill to stress-test the concept
- Divergent thinking → 8 idea variations
- Convergent thinking → Manager-first, visit-recording MVP
- Explicit "Not Doing" list to maintain focus

Result: **A sharp, de-risked MVP focused on data capture over feature breadth.**

---

**Built with:** Spring Boot (Kotlin) + PostgreSQL + React + Tailwind CSS

**Status:** Backend ✅ | Dashboard ✅ (needs Node upgrade) | Android 📋
