import { useState } from 'react';
import type { Salesman, Farm } from '../types';

interface FiltersProps {
  salesmen: Salesman[];
  farms: Farm[];
  onFilterChange: (salesmanId: string, farmId: string, days: number) => void;
}

export default function Filters({ salesmen, farms, onFilterChange }: FiltersProps) {
  const [selectedSalesman, setSelectedSalesman] = useState('');
  const [selectedFarm, setSelectedFarm] = useState('');
  const [selectedDays, setSelectedDays] = useState(30);

  const handleSalesmanChange = (value: string) => {
    setSelectedSalesman(value);
    onFilterChange(value, selectedFarm, selectedDays);
  };

  const handleFarmChange = (value: string) => {
    setSelectedFarm(value);
    onFilterChange(selectedSalesman, value, selectedDays);
  };

  const handleDaysChange = (value: number) => {
    setSelectedDays(value);
    onFilterChange(selectedSalesman, selectedFarm, value);
  };

  const handleReset = () => {
    setSelectedSalesman('');
    setSelectedFarm('');
    setSelectedDays(30);
    onFilterChange('', '', 30);
  };

  return (
    <div className="bg-white rounded-lg shadow p-6 mb-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Filters</h3>
        <button
          onClick={handleReset}
          className="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          Reset Filters
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Salesman Filter */}
        <div>
          <label htmlFor="salesman" className="block text-sm font-medium text-gray-700 mb-2">
            Salesman
          </label>
          <select
            id="salesman"
            value={selectedSalesman}
            onChange={(e) => handleSalesmanChange(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">All Salesmen</option>
            {salesmen.map((salesman) => (
              <option key={salesman.id} value={salesman.id}>
                {salesman.name} ({salesman.territory})
              </option>
            ))}
          </select>
        </div>

        {/* Farm Filter */}
        <div>
          <label htmlFor="farm" className="block text-sm font-medium text-gray-700 mb-2">
            Farm
          </label>
          <select
            id="farm"
            value={selectedFarm}
            onChange={(e) => handleFarmChange(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">All Farms</option>
            {farms.map((farm) => (
              <option key={farm.id} value={farm.id}>
                {farm.name}
              </option>
            ))}
          </select>
        </div>

        {/* Time Range Filter */}
        <div>
          <label htmlFor="timeRange" className="block text-sm font-medium text-gray-700 mb-2">
            Time Range
          </label>
          <select
            id="timeRange"
            value={selectedDays}
            onChange={(e) => handleDaysChange(Number(e.target.value))}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value={7}>Last 7 days</option>
            <option value={30}>Last 30 days</option>
            <option value={90}>Last 90 days</option>
            <option value={365}>Last year</option>
            <option value={0}>All time</option>
          </select>
        </div>
      </div>
    </div>
  );
}
