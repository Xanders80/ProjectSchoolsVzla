## Extended Few-shot: React Components
- Example 4: CollapsiblePanel
```tsx
export const CollapsiblePanel = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <details>
    <summary>{title}</summary>
    <div>{children}</div>
  </details>
);
```
- Example 5: DataCard with props
```tsx
export const DataCard = ({ data }: { data: { label: string; value: string }[] }) => (
  <div className="data-card">
    {data.map(d => (
      <div key={d.label} className="datum"><span className="label">{d.label}</span><span className="value">{d.value}</span></div>
    ))}
  </div>
);
```
