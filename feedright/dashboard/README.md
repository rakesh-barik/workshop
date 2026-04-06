# FeedRight Dashboard

Modern React dashboard for viewing and analyzing field visit data from FeedRight salesmen.

## Tech Stack

- **React 18** + **TypeScript**
- **Vite** - Fast build tool
- **Tailwind CSS** - Utility-first styling
- **Axios** - API client
- **date-fns** - Date formatting

## Features

### ✅ Implemented

1. **Real-time Visit Feed**
   - Display all visits from backend
   - Sortable table with all visit details
   - Salesman name, farm name, product details
   - Sync status indicator

2. **Smart Filters**
   - Filter by salesman
   - Filter by farm
   - Filter by time range (7/30/90/365 days, all time)
   - Reset filters button

3. **Summary Metrics Dashboard**
   - Total visits (all time)
   - Last 7 days visits
   - Last 30 days visits
   - Active farms count

4. **CSV Export**
   - Export filtered visits to CSV
   - Includes all visit details
   - Filename includes date

5. **Responsive Design**
   - Mobile-friendly layout
   - Grid adapts to screen size
   - Touch-friendly controls

## Prerequisites

⚠️ **IMPORTANT:** You need Node.js v14.18.0 or higher to run this project.

**Current Node version: v13.8.0 (too old)**

### Upgrade Node.js

**Option 1: Using Homebrew (macOS)**
```bash
brew upgrade node
```

**Option 2: Using nvm (Node Version Manager)**
```bash
# Install nvm if you don't have it
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Install and use Node 18 (LTS)
nvm install 18
nvm use 18
```

**Option 3: Download from nodejs.org**
Visit https://nodejs.org and download the LTS version.

## Quick Start

Once you have Node v14.18+:

### 1. Install Dependencies

```bash
cd /Users/ibhan/workshop/feedright/dashboard
npm install
```

### 2. Start Development Server

```bash
npm run dev
```

The dashboard will start on **http://localhost:3000**

### 3. Build for Production

```bash
npm run build
npm run preview
```

## Backend Connection

The dashboard connects to the Spring Boot backend via proxy:

- **Backend URL:** http://localhost:8080
- **Proxy configured in:** `vite.config.ts`
- **All `/api/*` requests** are proxied to the backend

**Make sure the backend is running before starting the dashboard!**

```bash
# In another terminal, start the backend
cd /Users/ibhan/workshop/feedright/backend
./gradlew bootRun
```

## Project Structure

```
dashboard/
├── src/
│   ├── api/
│   │   └── client.ts          # Axios API client
│   ├── components/
│   │   ├── Filters.tsx        # Filter controls
│   │   ├── StatsCard.tsx      # Metric cards
│   │   └── VisitTable.tsx     # Visit data table
│   ├── pages/
│   │   └── Dashboard.tsx      # Main dashboard page
│   ├── types/
│   │   └── index.ts           # TypeScript types
│   ├── App.tsx                # Root component
│   ├── main.tsx               # Entry point
│   └── index.css              # Tailwind directives
├── public/
├── index.html                  # HTML entry point
├── vite.config.ts             # Vite configuration
├── tailwind.config.js         # Tailwind config
├── postcss.config.js          # PostCSS config
├── tsconfig.json              # TypeScript config
└── package.json               # Dependencies
```

## API Integration

The dashboard consumes these backend endpoints:

### Visits
- `GET /api/visits` - Get all visits
- `GET /api/visits/salesman/{id}` - Get visits by salesman
- `GET /api/visits/range?start=&end=` - Get visits by date range

### Salesmen
- `GET /api/salesmen` - Get all salesmen

### Farms
- `GET /api/farms` - Get all farms

### Products
- `GET /api/products?activeOnly=true` - Get active products

## Features in Detail

### Dashboard Metrics

The dashboard calculates and displays:
- **Total Visits:** All visits in the database
- **Last 7 Days:** Visit count in the past week
- **Last 30 Days:** Visit count in the past month
- **Active Farms:** Number of unique farms visited

### Filtering System

Users can filter visits by:
1. **Salesman:** Select from dropdown (shows territory)
2. **Farm:** Select from dropdown
3. **Time Range:** 7/30/90/365 days or all time

Filters are applied **client-side** for instant results. The "Reset Filters" button clears all filters.

### CSV Export

The export function:
1. Uses filtered visit data (respects current filters)
2. Formats as CSV with proper escaping
3. Downloads with filename: `feedright-visits-YYYY-MM-DD.csv`
4. Includes columns: Date, Salesman, Farm, Product, SKU, Quantity, Notes, Synced At

## Development Notes

### TypeScript Types

All backend DTOs are typed in `src/types/index.ts`:
- `Visit` - Visit record with all details
- `Salesman` - Salesman info
- `Farm` - Farm info
- `Product` - Product catalog entry

### State Management

Uses **React hooks** (no external state library):
- `useState` for component state
- `useEffect` for data loading
- `useMemo` for derived state (filters, stats)

### Performance

- **Memoized calculations** - Stats and filters use `useMemo`
- **Client-side filtering** - Instant filtering without backend calls
- **Optimized renders** - Components only re-render when necessary

### Responsive Design

Built with Tailwind's responsive classes:
- Mobile: Single column layout
- Tablet: 2-column grid
- Desktop: 4-column grid for metrics

## Troubleshooting

### Dashboard won't load

**Error:** "Failed to load data from backend"

**Solution:**
1. Make sure backend is running on port 8080
2. Check backend logs for errors
3. Test backend directly: `curl http://localhost:8080/api/visits`

### Vite won't start

**Error:** "Unexpected reserved word" or syntax errors

**Solution:**
- Upgrade Node.js to v14.18+ (see Prerequisites above)
- Delete `node_modules` and `package-lock.json`
- Run `npm install` again

### Port 3000 already in use

**Solution:**
```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9

# Or change port in vite.config.ts
```

## Future Enhancements

Potential features for future iterations:

- [ ] Date range picker (instead of preset ranges)
- [ ] Charts and graphs (visit trends over time)
- [ ] Territory-based filtering
- [ ] Product recommendation insights
- [ ] Real-time updates (WebSocket)
- [ ] Visit detail modal
- [ ] Bulk actions
- [ ] User authentication
- [ ] Dark mode

## Current Status

✅ **All core features implemented and ready to test**

**Next step:** Upgrade Node.js and run `npm run dev`
