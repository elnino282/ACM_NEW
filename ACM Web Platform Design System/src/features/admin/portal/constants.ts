import type { AdminView, AdminViewConfig } from './types';

export const ADMIN_VIEW_CONFIG: Record<AdminView, AdminViewConfig> = {
  dashboard: { title: 'Dashboard' },
  users: { title: 'User Management', breadcrumbLabel: 'User Management' },
  farmers: { title: 'Farmer Management', breadcrumbLabel: 'Farmer Management' },
  buyers: { title: 'Buyer Management', breadcrumbLabel: 'Buyer Management' },
  contracts: { title: 'Contracts', breadcrumbLabel: 'Contracts' },
  documents: { title: 'Documents', breadcrumbLabel: 'Documents' },
  reports: { title: 'Reports', breadcrumbLabel: 'Reports' },
  monitoring: { title: 'Monitoring', breadcrumbLabel: 'Monitoring' },
  settings: { title: 'Settings', breadcrumbLabel: 'Settings' },
};

export const getAdminViewTitle = (view: AdminView): string =>
  ADMIN_VIEW_CONFIG[view]?.title ?? ADMIN_VIEW_CONFIG.dashboard.title;

export const getAdminBreadcrumbLabel = (view: AdminView): string | undefined =>
  ADMIN_VIEW_CONFIG[view]?.breadcrumbLabel;
