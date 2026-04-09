## Few-shot: React Components
- Example 1: Simple Card
```tsx
import React from 'react';
export const Card = ({ title, children }: { title: string; children?: React.ReactNode }) => (
  <div className="card">
    <div className="card-header">{title}</div>
    <div className="card-body">{children}</div>
  </div>
);
```
- Example 2: List
```tsx
export const List = ({ items }: { items: string[] }) => (
  <ul>{items.map((i) => (<li key={i}>{i}</li>))}</ul>
);
```
- Example 3: Form Field
```tsx
export const TextField = ({ label, value, onChange }: { label: string; value: string; onChange: (v: string)=>void }) => (
  <label>{label}<input value={value} onChange={e => onChange(e.target.value)} /></label>
);
```
