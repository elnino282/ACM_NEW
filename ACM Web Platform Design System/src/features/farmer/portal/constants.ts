import type { FarmerView, FarmerViewConfig } from './types';

/**
 * Configuration mapping for all farmer portal views
 */
export const FARMER_VIEW_CONFIG: Record<FarmerView, FarmerViewConfig> = {
  dashboard: {
    title: 'Dashboard',
    breadcrumbLabel: 'Dashboard',
  },
  farms: {
    title: 'Farm Management',
    breadcrumbLabel: 'Farm Management',
  },
  plots: {
    title: 'Plot Management',
    breadcrumbLabel: 'Plot Management',
  },
  seasons: {
    title: 'Season Management',
    breadcrumbLabel: 'Season Management',
  },
  tasks: {
    title: 'Tasks Workspace',
    breadcrumbLabel: 'Tasks Workspace',
  },
  crops: {
    title: 'Crop Management',
    breadcrumbLabel: 'Crop Management',
  },
  expenses: {
    title: 'Expense Management',
    breadcrumbLabel: 'Expense Management',
  },
  harvest: {
    title: 'Harvest Management',
    breadcrumbLabel: 'Harvest Management',
  },
  sales: {
    title: 'Sale Management',
    breadcrumbLabel: 'Sale Management',
  },
  reports: {
    title: 'Reports',
    breadcrumbLabel: 'Reports',
  },
  documents: {
    title: 'Documents',
    breadcrumbLabel: 'Documents',
  },
  profile: {
    title: 'Profile',
    breadcrumbLabel: 'Profile',
  },
};

/**
 * Get the display title for a farmer view
 */
export function getFarmerViewTitle(view: FarmerView): string {
  return FARMER_VIEW_CONFIG[view]?.title ?? 'Dashboard';
}

/**
 * Get the breadcrumb label for a farmer view
 */
export function getFarmerBreadcrumbLabel(view: FarmerView): string {
  return FARMER_VIEW_CONFIG[view]?.breadcrumbLabel ?? 'Dashboard';
}
