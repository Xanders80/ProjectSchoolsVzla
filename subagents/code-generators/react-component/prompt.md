# React Component Generator

Genera componentes React reutilizables.

## Template
```tsx
import React from 'react';
import './${ComponentName}.module.css';

interface ${ComponentName}Props {
  title: string;
  children?: React.ReactNode;
}

export const ${ComponentName}: React.FC<${ComponentName}Props> = ({ title, children }) => {
  return (
    <div className="card shadow mb-4">
      <div className="card-header py-3">
        <h6 className="m-0 font-weight-bold text-primary">{title}</h6>
      </div>
      <div className="card-body">
        {children}
      </div>
    </div>
  );
};
```
