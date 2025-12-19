import httpClient from '@/shared/api/http';
import { parseApiResponse } from '@/shared/api/types';
import { z } from 'zod';
import {
    StockMovementSchema,
    StockMovementRequestSchema,
    OnHandParamsSchema,
} from '../model/schemas';
import type {
    StockMovement,
    StockMovementRequest,
    OnHandParams,
} from '../model/types';

export const inventoryApi = {
    /** POST /api/v1/inventory/movements - Record stock movement (inbound/outbound) */
    recordMovement: async (data: StockMovementRequest): Promise<StockMovement> => {
        const validatedPayload = StockMovementRequestSchema.parse(data);
        const response = await httpClient.post('/api/v1/inventory/movements', validatedPayload);
        return parseApiResponse(response.data, StockMovementSchema);
    },

    /** GET /api/v1/inventory/lots/{lotId}/on-hand - Get current on-hand quantity */
    getOnHand: async (lotId: number, params: OnHandParams): Promise<number> => {
        const validatedParams = OnHandParamsSchema.parse(params);
        const response = await httpClient.get(`/api/v1/inventory/lots/${lotId}/on-hand`, { params: validatedParams });
        return parseApiResponse(response.data, z.number());
    },
};
