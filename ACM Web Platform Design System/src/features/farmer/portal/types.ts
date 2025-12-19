import type { BreadcrumbPath } from '@/features/shared/layout/types';

/**
 * Available views in the farmer portal
 */
export type FarmerView =
  | 'dashboard'
  | 'farms'
  | 'plots'
  | 'seasons'
  | 'tasks'
  | 'crops'
  | 'expenses'
  | 'harvest'
  | 'sales'
  | 'reports'
  | 'documents'
  | 'profile';

/**
 * Configuration for each farmer view
 */
export interface FarmerViewConfig {
  title: string;
  breadcrumbLabel: string;
}

/**
 * Function type for building breadcrumbs
 */
export type BuildBreadcrumbs = (view: FarmerView) => BreadcrumbPath[];
