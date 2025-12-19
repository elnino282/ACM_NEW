import type { AdminView } from '../types';
import { getAdminViewTitle } from '../constants';
import { UnderConstruction } from './UnderConstruction';

// Feature Imports
// Note: Ensure these paths match your tsconfig aliases and directory structure
import { AdminDashboard } from '@/features/admin';
import { FarmerManagement } from '@/features/admin/farmer-management';
import BuyerManagement from '@/features/admin/buyer-management';
import { DocumentManagement } from '@/features/admin/document-management';
import { ReportsAnalytics } from '@/features/admin/reports-analytics';
import { SystemMonitoring } from '@/features/admin/system-monitoring';
import { SystemSettings } from '@/features/admin/system-settings';

type AdminPortalContentProps = {
  currentView: AdminView;
};

export function AdminPortalContent({ currentView }: AdminPortalContentProps) {
  switch (currentView) {
    case 'dashboard':
      return <AdminDashboard />;
    case 'farmers':
      return <FarmerManagement />;
    case 'buyers':
      return <BuyerManagement />;
    case 'documents':
      return <DocumentManagement />;
    case 'reports':
      return <ReportsAnalytics />;
    case 'monitoring':
      return <SystemMonitoring />;
    case 'settings':
      return <SystemSettings />;
    default:
      return <UnderConstruction title={getAdminViewTitle(currentView)} />;
  }
}
