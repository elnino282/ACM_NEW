import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/shared/ui';
import { AlertTriangle, AlertCircle } from 'lucide-react';

/**
 * Incidents Page (Placeholder)
 * 
 * This is a placeholder page for the Incidents feature.
 * The incident entity already exists in the system.
 */
export function IncidentsPage() {
    return (
        <div className="container mx-auto p-6 space-y-6">
            {/* Page Header */}
            <div className="flex items-center gap-3">
                <AlertTriangle className="w-8 h-8 text-[#2F9E44]" />
                <div>
                    <h1 className="text-3xl font-bold text-[#333333]">Incidents</h1>
                    <p className="text-[#777777]">Sự cố</p>
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
                        Report and track farm incidents
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-[#555555]">
                        The Incidents Management feature is currently under development. This feature will allow you to:
                    </p>
                    <ul className="list-disc list-inside space-y-2 text-[#555555] ml-4">
                        <li>Report farm incidents with severity levels (high, medium, low)</li>
                        <li>Track pest infestations and disease outbreaks</li>
                        <li>Document equipment failures and maintenance issues</li>
                        <li>Record weather-related damage and natural disasters</li>
                        <li>Link incidents to specific plots and seasons</li>
                        <li>Monitor incident resolution status and follow-up actions</li>
                        <li>Generate incident reports and analytics</li>
                    </ul>
                    <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                        <p className="text-sm text-blue-900">
                            <strong>Note:</strong> The incident entity already exists in the system at{' '}
                            <code className="bg-blue-100 px-1 py-0.5 rounded">src/entities/incident</code>.
                            Incident alerts are already displayed on the dashboard. The full incident management UI is coming soon.
                        </p>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}
