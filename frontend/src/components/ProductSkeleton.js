import React from 'react';

function ProductSkeleton() {
    return (
        <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-md p-5 flex flex-col h-[420px] animate-pulse border dark:border-gray-700">
            {/* Top Buttons Skeleton */}
            <div className="flex gap-2 mb-4">
                <div className="h-10 bg-gray-200 dark:bg-gray-700 rounded-xl flex-1"></div>
                <div className="h-10 bg-gray-200 dark:bg-gray-700 rounded-xl flex-1"></div>
            </div>

            {/* Image Skeleton */}
            <div className="w-full h-40 bg-gray-200 dark:bg-gray-700 rounded-xl mb-4"></div>

            {/* Title Skeleton */}
            <div className="h-5 bg-gray-200 dark:bg-gray-700 rounded-md w-3/4 mb-3"></div>
            <div className="h-5 bg-gray-200 dark:bg-gray-700 rounded-md w-1/2 mb-4"></div>

            {/* Description Skeleton */}
            <div className="h-3 bg-gray-100 dark:bg-gray-700/60 rounded-md w-full mb-2"></div>
            <div className="h-3 bg-gray-100 dark:bg-gray-700/60 rounded-md w-5/6 mb-4 flex-1"></div>

            {/* Price & Rating Skeleton */}
            <div className="flex justify-between items-center mb-4">
                <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-md w-24"></div>
                <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-md w-12"></div>
            </div>

            {/* Button Skeleton */}
            <div className="h-11 bg-gray-200 dark:bg-gray-700 rounded-xl w-full"></div>
        </div>
    );
}

export default ProductSkeleton;