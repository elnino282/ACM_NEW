import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/shared/ui';
import { Package, AlertCircle } from 'lucide-react';

/**
 * Inventory Page (Placeholder)
 * 
 * This is a placeholder page for the Inventory feature.
 * The inventory entity already exists in the system.
 */
export function InventoryPage() {
    return (
        <div className="container mx-auto p-6 space-y-6">
            {/* Page Header */}
            <div className="flex items-center gap-3">
                <Package className="w-8 h-8 text-[#2F9E44]" />
                <div>
                    <h1 className="text-3xl font-bold text-[#333333]">Inventory</h1>
                    <p className="text-[#777777]">Kho vật tư & tồn</p>
                </div>
            </div>

            {/* Under Construction Card */}
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <AlertCircle className="w-5 h-5 text-amber-500" />
                        Under Construction
                    </CardTitle>
                    <CardDescription>
                        Manage warehouses, locations, lots, and stock movements
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-[#555555]">
                        The Inventory Management feature is currently under development. This feature will allow you to:
                    </p>
                    <ul className="list-disc list-inside space-y-2 text-[#555555] ml-4">
                        <li>Manage multiple warehouses and storage locations</li>
                        <li>Track inventory lots with batch numbers and expiry dates</li>
                        <li>Record stock movements (inbound/outbound)</li>
                        <li>Monitor current on-hand quantities</li>
                        <li>View inventory history and audit trails</li>
                        <li>Set minimum stock levels and receive low-stock alerts</li>
                    </ul>
                    <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                        <p className="text-sm text-blue-900">
                            <strong>Note:</strong> The inventory entity already exists in the system at{' '}
                            <code className="bg-blue-100 px-1 py-0.5 rounded">src/entities/inventory</code>.
                            The full UI implementation is coming soon.
                        </p>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}
