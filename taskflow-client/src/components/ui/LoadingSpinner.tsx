import React from 'react';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg' | 'xl';
  centered?: boolean; // Prop to center the loader
  className?: string; // Prop for colors or extra margin
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ 
  size = 'md', 
  centered = false,
  className = '' 
}) => {
  
  // Dict of sizes
  const sizeClasses = {
    sm: 'h-5 w-5 border-2',      // For buttons
    md: 'h-8 w-8 border-[3px]',  // Standard
    lg: 'h-12 w-12 border-4',    // For big sections
    xl: 'h-16 w-16 border-4',    // For the main page loading
  };

  const spinner = (
    <div
      className={`
        animate-spin rounded-full 
        border-gray-700           
        border-t-indigo-500       
        ${sizeClasses[size]}
        ${className}
      `}
      role="status"
    >
      <span className="sr-only">Cargando...</span>
    </div>
  );

  // If centered wrap with a flex div
  if (centered) {
    return (
      <div className="flex justify-center items-center w-full py-10">
        {spinner}
      </div>
    );
  }

  return spinner;
};