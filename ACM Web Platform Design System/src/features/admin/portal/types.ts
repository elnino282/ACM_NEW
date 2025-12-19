import type { BreadcrumbPath } from '@/features/shared/layout/types';

export type AdminView =
  | 'dashboard'
  | 'users'
  | 'farmers'
  | 'buyers'
  | 'contracts'
  | 'documents'
  | 'reports'
  | 'monitoring'
  | 'settings';

export type AdminViewConfig = {
  title: string;
  breadcrumbLabel?: string;
};

// Type definition helpers
export type BuildBreadcrumbs = (view: AdminView) => BreadcrumbPath[];
