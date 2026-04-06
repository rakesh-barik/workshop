import { useState, useEffect, useMemo } from 'react';
import { visitApi, salesmanApi, farmApi } from '../api/client';
import type { Visit, Salesman, Farm } from '../types';
import StatsCard from '../components/StatsCard';
import VisitTable from '../components/VisitTable';
import Filters from '../components/Filters';

export default function Dashboard() {
  const [visits, setVisits] = useState<Visit[]>([]);
  const [salesmen, setSalesmen] = useState<Salesman[]>([]);
  const [farms, setFarms] = useState<Farm[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filter state
  const [selectedSalesman, setSelectedSalesman] = useState('');
  const [selectedFarm, setSelectedFarm] = useState('');
  const [selectedDays, setSelectedDays] = useState(30);

  // Load initial data
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [visitsData, salesmenData, farmsData] = await Promise.all([
          visitApi.getAll(),
          salesmanApi.getAll(),
          farmApi.getAll(),
        ]);

        setVisits(visitsData);
        setSalesmen(salesmenData);
        setFarms(farmsData);
        setError(null);
      } catch (err) {
        console.error('Error loading data:', err);
        setError('Failed to load data from backend');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Filter visits based on selected filters
  const filteredVisits = useMemo(() => {
    let filtered = [...visits];

    // Filter by salesman
    if (selectedSalesman) {
      filtered = filtered.filter(v => v.salesmanId === selectedSalesman);
    }

    // Filter by farm
    if (selectedFarm) {
      filtered = filtered.filter(v => v.farmId === selectedFarm);
    }

    // Filter by date range
    if (selectedDays > 0) {
      const cutoffDate = new Date();
      cutoffDate.setDate(cutoffDate.getDate() - selectedDays);
      filtered = filtered.filter(v => new Date(v.visitDate) >= cutoffDate);
    }

    return filtered;
  }, [visits, selectedSalesman, selectedFarm, selectedDays]);

  // Calculate stats
  const stats = useMemo(() => {
    const now = new Date();
    const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);

    const last7Days = visits.filter(v => new Date(v.visitDate) >= sevenDaysAgo).length;
    const last30Days = visits.filter(v => new Date(v.visitDate) >= thirtyDaysAgo).length;

    const uniqueFarms = new Set(visits.map(v => v.farmId)).size;
    const uniqueSalesmen = new Set(visits.map(v => v.salesmanId)).size;

    return {
      totalVisits: visits.length,
      last7Days,
      last30Days,
      uniqueFarms,
      uniqueSalesmen,
    };
  }, [visits]);

  // Handle filter changes
  const handleFilterChange = (salesmanId: string, farmId: string, days: number) => {
    setSelectedSalesman(salesmanId);
    setSelectedFarm(farmId);
    setSelectedDays(days);
  };

  // Export to CSV
  const handleExportCSV = () => {
    if (filteredVisits.length === 0) {
      alert('No visits to export');
      return;
    }

    const headers = ['Date', 'Salesman', 'Farm', 'Product', 'SKU', 'Quantity (kg)', 'Notes', 'Synced At'];
    const rows = filteredVisits.map(visit => [
      visit.visitDate,
      visit.salesmanName,
      visit.farmName,
      visit.productName,
      visit.productSku,
      visit.quantity.toString(),
      visit.notes || '',
      visit.syncedAt || '',
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(',')),
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', `feedright-visits-${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="text-red-600 text-5xl mb-4">⚠️</div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Error Loading Dashboard</h2>
          <p className="text-gray-600">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">FeedRight Dashboard</h1>
              <p className="mt-1 text-sm text-gray-500">Sales manager visit tracking</p>
            </div>
            <div className="text-sm text-gray-500">
              Last updated: {new Date().toLocaleString()}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatsCard
            title="Total Visits"
            value={stats.totalVisits}
            description="All time"
            icon="📊"
          />
          <StatsCard
            title="Last 7 Days"
            value={stats.last7Days}
            description="Recent activity"
            icon="📅"
          />
          <StatsCard
            title="Last 30 Days"
            value={stats.last30Days}
            description="Monthly total"
            icon="📈"
          />
          <StatsCard
            title="Active Farms"
            value={stats.uniqueFarms}
            description={`${salesmen.length} salesmen`}
            icon="🏘️"
          />
        </div>

        {/* Filters */}
        <Filters
          salesmen={salesmen}
          farms={farms}
          onFilterChange={handleFilterChange}
        />

        {/* Results Count */}
        <div className="mb-4">
          <p className="text-sm text-gray-600">
            Showing <span className="font-semibold">{filteredVisits.length}</span> of{' '}
            <span className="font-semibold">{visits.length}</span> total visits
          </p>
        </div>

        {/* Visit Table */}
        <VisitTable visits={filteredVisits} onExportCSV={handleExportCSV} />
      </main>
    </div>
  );
}
