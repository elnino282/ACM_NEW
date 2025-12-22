import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/shared/ui';
import { FileText, AlertCircle } from 'lucide-react';

/**
 * Field Logs Page (Placeholder)
 * 
 * This is a placeholder page for the Field Logs feature.
 * The field-log entity already exists in the system.
 */
export function FieldLogsPage() {
    return (
        <div className="container mx-auto p-6 space-y-6">
            {/* Page Header */}
            <div className="flex items-center gap-3">
                <FileText className="w-8 h-8 text-[#2F9E44]" />
                <div>
                    <h1 className="text-3xl font-bold text-[#333333]">Field Logs</h1>
                    <p className="text-[#777777]">Nhật ký đồng ruộng</p>
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
                        Track daily field activities and observations
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-[#555555]">
                        The Field Logs feature is currently under development. This feature will allow you to:
                    </p>
                    <ul className="list-disc list-inside space-y-2 text-[#555555] ml-4">
                        <li>Record daily field observations and activities</li>
                        <li>Track weather conditions and environmental factors</li>
                        <li>Document pest and disease observations</li>
                        <li>Monitor crop growth stages</li>
                        <li>Link logs to specific seasons and plots</li>
                    </ul>
                    <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                        <p className="text-sm text-blue-900">
                            <strong>Note:</strong> The field-log entity already exists in the system at{' '}
                            <code className="bg-blue-100 px-1 py-0.5 rounded">src/entities/field-log</code>.
                            The full UI implementation is coming soon.
                        </p>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}
