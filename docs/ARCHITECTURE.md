# POSPedia Architecture

## Scope
POSPedia v0.1 is an Android POS vertical slice designed for multi-tenant operation and offline persistence.

## Layers
- `domain/`: business models such as Product and CartItem.
- `data/`: SQLite persistence, tenant/outlet-scoped repository, and persistent session.
- `util/`: presentation-safe utilities such as Indonesian currency formatting.
- `MainActivity`: navigation/screen orchestration for the first UAT slice. Further feature growth should split screens into dedicated Activities/Fragments/ViewModels.

## Tenant boundary
Every product and transaction query requires both `tenant_id` and `outlet_id`. Client-side tenant scope is for local isolation only; a future backend must derive authorized tenant/outlet memberships from authenticated server-side identity.

## Offline-first
SQLite persists products, transaction headers, and transaction items. SharedPreferences persists the demo session. Transactions survive process/app restarts.

## Future API synchronization
A later release should add a sync queue/outbox with server IDs, sync status, retry count, timestamps and conflict policy. Server authorization remains authoritative.

## Receipt
The receipt presentation is structured for a 58mm thermal-printer adapter. Bluetooth transport is intentionally not enabled until printer hardware/protocol selection is finalized.

## UAT credentials
Tenant: DEMO
Email: cashier@demo.id
Password: demo


## Expanded MVP modules
- Role model: PLATFORM_ADMIN, TENANT_OWNER, TENANT_ADMIN, OUTLET_MANAGER, CASHIER.
- Catalog: product/category persistence, SKU/barcode-ready schema, active/deactivate architecture.
- Inventory: stock deduction on completed sales, adjustment/history schema, low-stock query.
- Customers: tenant-scoped local customer records.
- Payments: Cash, QRIS, Bank Transfer, Debit Card, Credit Card, Other enum plus provider interface for future gateway adapters.
- Reports: local daily summary and payment-method aggregation.
- Receipt: 58mm/80mm paper abstraction prepared for printer adapters.

## Production boundaries
Google OAuth, remote password reset, QRIS/card acquiring, cloud synchronization, Bluetooth printer transport, platform administration and server-enforced authorization require external services/credentials and are not simulated as successful integrations in the offline UAT build.
