import { z } from 'zod';

// ═══════════════════════════════════════════════════════════════
// STOCK MOVEMENT REQUEST
// ═══════════════════════════════════════════════════════════════

export const StockMovementRequestSchema = z.object({
    supplyLotId: z.number().int().positive('Supply lot ID is required'),
    warehouseId: z.number().int().positive('Warehouse ID is required'),
    locationId: z.number().int().optional(),
    movementType: z.string().min(1, 'Movement type is required'),
    quantity: z.number().min(1, 'Quantity must be at least 1'),
    seasonId: z.number().int().optional(),
    taskId: z.number().int().optional(),
    note: z.string().optional(),
});

export type StockMovementRequest = z.infer<typeof StockMovementRequestSchema>;

// ═══════════════════════════════════════════════════════════════
// STOCK MOVEMENT RESPONSE
// ═══════════════════════════════════════════════════════════════

export const StockMovementSchema = z.object({
    id: z.number().int().positive(),
    supplyLotId: z.number().int().positive(),
    warehouseId: z.number().int().positive(),
    locationId: z.number().int().optional().nullable(),
    movementType: z.string(),
    quantity: z.number(),
    seasonId: z.number().int().optional().nullable(),
    taskId: z.number().int().optional().nullable(),
    note: z.string().optional().nullable(),
    createdAt: z.string().datetime().optional(),
    updatedAt: z.string().datetime().optional(),
});

export type StockMovement = z.infer<typeof StockMovementSchema>;

// ═══════════════════════════════════════════════════════════════
// ON-HAND QUERY PARAMS
// ═══════════════════════════════════════════════════════════════

export const OnHandParamsSchema = z.object({
    warehouseId: z.number().int().positive('Warehouse ID is required'),
    locationId: z.number().int().optional(),
});

export type OnHandParams = z.infer<typeof OnHandParamsSchema>;
