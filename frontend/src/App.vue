<template>
  <router-view />
</template>

<script setup>
</script>

<style>
:root {
  --mi-primary: #FF6900;
  --mi-primary-light: #FF8A3D;
  --mi-primary-lighter: #FFF4EC;
  --mi-primary-dark: #E65C00;
  --mi-text-primary: #1f1f1f;
  --mi-text-regular: #555;
  --mi-text-secondary: #999;
  --mi-text-placeholder: #bbb;
  --mi-border-color: #e8e8e8;
  --mi-bg-page: #f5f5f5;
  --mi-bg-card: #ffffff;
  --mi-shadow-sm: 0 1px 4px rgba(0, 0, 0, 0.04);
  --mi-shadow-md: 0 2px 12px rgba(0, 0, 0, 0.06);
  --mi-shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.08);
  --mi-radius-sm: 6px;
  --mi-radius-md: 10px;
  --mi-radius-lg: 16px;
  --mi-transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  --mi-font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--mi-font-family);
  background-color: var(--mi-bg-page);
  color: var(--mi-text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
}

a {
  text-decoration: none;
  color: inherit;
}

::-webkit-scrollbar {
  width: 5px;
  height: 5px;
}

::-webkit-scrollbar-thumb {
  background: #d4d4d4;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

::-webkit-scrollbar-track {
  background: transparent;
}

/* Element Plus Xiaomi Theme Overrides */
.el-button--primary {
  --el-button-bg-color: var(--mi-primary) !important;
  --el-button-border-color: var(--mi-primary) !important;
  --el-button-hover-bg-color: var(--mi-primary-light) !important;
  --el-button-hover-border-color: var(--mi-primary-light) !important;
  --el-button-active-bg-color: var(--mi-primary-dark) !important;
  --el-button-active-border-color: var(--mi-primary-dark) !important;
}

.el-button--primary.is-link {
  --el-button-text-color: var(--mi-primary) !important;
  --el-button-hover-text-color: var(--mi-primary-light) !important;
}

.el-pagination.is-background .el-pager li.is-active {
  background-color: var(--mi-primary) !important;
}

.el-tag--primary.is-light {
  --el-tag-bg-color: var(--mi-primary-lighter) !important;
  --el-tag-text-color: var(--mi-primary) !important;
  --el-tag-border-color: transparent !important;
}

.el-table .el-table__header th {
  background-color: #fafafa !important;
  color: var(--mi-text-primary) !important;
  font-weight: 600 !important;
  font-size: 13px !important;
}

.el-table .el-table__row {
  transition: background-color 0.2s ease !important;
}

.el-table .el-table__row:hover > td.el-table__cell {
  background-color: #fafafa !important;
}

.el-dialog {
  border-radius: var(--mi-radius-md) !important;
  overflow: hidden;
}

.el-dialog__header {
  border-bottom: 1px solid var(--mi-border-color) !important;
  padding: 18px 24px !important;
  margin: 0 !important;
}

.el-dialog__title {
  font-weight: 600 !important;
  color: var(--mi-text-primary) !important;
  font-size: 16px !important;
}

.el-dialog__body {
  padding: 28px 24px !important;
}

.el-dialog__footer {
  border-top: 1px solid var(--mi-border-color) !important;
  padding: 14px 24px !important;
}

.el-input__wrapper {
  border-radius: var(--mi-radius-sm) !important;
  transition: var(--mi-transition) !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--mi-primary) inset !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--mi-primary) inset !important;
}

.el-select .el-input__wrapper {
  border-radius: var(--mi-radius-sm) !important;
}

.el-form-item__label {
  color: var(--mi-text-regular) !important;
  font-weight: 500 !important;
}

.el-radio__input.is-checked .el-radio__inner {
  border-color: var(--mi-primary) !important;
  background-color: var(--mi-primary) !important;
}

.el-radio__input.is-checked + .el-radio__label {
  color: var(--mi-primary) !important;
}

.el-message-box {
  border-radius: var(--mi-radius-md) !important;
  border: none !important;
  box-shadow: var(--mi-shadow-lg) !important;
}

.el-message {
  border-radius: var(--mi-radius-sm) !important;
  border: none !important;
}

.el-empty {
  padding: 40px 0;
}

.el-empty__description p {
  color: var(--mi-text-secondary) !important;
  font-size: 14px !important;
}

.el-select-dropdown__item.is-selected {
  color: var(--mi-primary) !important;
  font-weight: 600 !important;
}

/* Global Animations */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-20px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.animate-fade-in-up {
  animation: fadeInUp 0.5s ease-out;
}

.animate-fade-in-left {
  animation: fadeInLeft 0.5s ease-out;
}

.animate-slide-in {
  animation: slideIn 0.3s ease-out;
}

.page-card {
  animation: fadeInUp 0.4s ease-out;
}

.stat-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.05s; }
.stat-card:nth-child(2) { animation-delay: 0.1s; }
.stat-card:nth-child(3) { animation-delay: 0.15s; }
.stat-card:nth-child(4) { animation-delay: 0.2s; }

.quick-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quick-card:hover {
  animation: pulse 0.3s ease;
}

.el-table {
  animation: fadeInUp 0.4s ease-out;
}

.el-dialog {
  animation: slideIn 0.3s ease-out;
}

/* Loading shimmer */
@keyframes shimmer {
  0% { background-position: -200px 0; }
  100% { background-position: calc(200px + 100%) 0; }
}
</style>
