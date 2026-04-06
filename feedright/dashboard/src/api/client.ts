import axios from 'axios';
import type { Visit, Salesman, Farm, Product } from '../types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const visitApi = {
  getAll: async (): Promise<Visit[]> => {
    const response = await api.get<Visit[]>('/visits');
    return response.data;
  },

  getBySalesman: async (salesmanId: string): Promise<Visit[]> => {
    const response = await api.get<Visit[]>(`/visits/salesman/${salesmanId}`);
    return response.data;
  },

  getByDateRange: async (start: string, end: string): Promise<Visit[]> => {
    const response = await api.get<Visit[]>('/visits/range', {
      params: { start, end },
    });
    return response.data;
  },
};

export const salesmanApi = {
  getAll: async (): Promise<Salesman[]> => {
    const response = await api.get<Salesman[]>('/salesmen');
    return response.data;
  },

  getById: async (id: string): Promise<Salesman> => {
    const response = await api.get<Salesman>(`/salesmen/${id}`);
    return response.data;
  },

  getByTerritory: async (territory: string): Promise<Salesman[]> => {
    const response = await api.get<Salesman[]>(`/salesmen/territory/${territory}`);
    return response.data;
  },
};

export const farmApi = {
  getAll: async (): Promise<Farm[]> => {
    const response = await api.get<Farm[]>('/farms');
    return response.data;
  },

  getById: async (id: string): Promise<Farm> => {
    const response = await api.get<Farm>(`/farms/${id}`);
    return response.data;
  },

  getByTerritory: async (territory: string): Promise<Farm[]> => {
    const response = await api.get<Farm[]>(`/farms/territory/${territory}`);
    return response.data;
  },
};

export const productApi = {
  getAll: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/products');
    return response.data;
  },

  getActive: async (): Promise<Product[]> => {
    const response = await api.get<Product[]>('/products', {
      params: { activeOnly: true },
    });
    return response.data;
  },

  getByCategory: async (category: string): Promise<Product[]> => {
    const response = await api.get<Product[]>(`/products/category/${category}`);
    return response.data;
  },
};

export default api;
