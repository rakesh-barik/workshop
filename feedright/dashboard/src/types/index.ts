export interface Salesman {
  id: string;
  name: string;
  phone: string;
  territory: string;
}

export interface Farm {
  id: string;
  name: string;
  location: string;
  territory: string;
}

export interface Product {
  id: string;
  sku: string;
  name: string;
  category: string;
  isActive: boolean;
}

export interface Visit {
  id: string;
  salesmanId: string;
  salesmanName: string;
  farmId: string;
  farmName: string;
  productId: string;
  productName: string;
  productSku: string;
  quantity: number;
  visitDate: string;
  notes: string | null;
  createdAt: string;
  syncedAt: string | null;
}

export interface VisitFilters {
  salesmanId?: string;
  farmId?: string;
  startDate?: string;
  endDate?: string;
}

export interface DashboardStats {
  totalVisits: number;
  totalVisitsLast7Days: number;
  totalVisitsLast30Days: number;
  activeFarms: number;
  activeSalesmen: number;
}
