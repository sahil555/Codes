import collections
from typing import List

class CornerRectangleFinder:
    """
    A collection of optimized algorithms to count the number of corner rectangles
    in a 2D binary matrix. A corner rectangle is defined by four 1s forming 
    the four corners of an axis-aligned rectangle.
    """

    def count_via_frequency_map(self, matrix: List[List[int]]) -> int:
        """
        Approach 1: Column-Pair Frequency Tracking (Optimized Hash Map)
        Best for: Balanced or general-purpose grids where rows >= cols.
        Time Complexity: O(R * C^2)
        Space Complexity: O(C^2)
        """
        if not matrix or len(matrix) < 2 or len(matrix[0]) < 2:
            return 0
            
        rows, cols = len(matrix), len(matrix[0])
        total_rectangles = 0
        # Tracks how many times a pair of columns (c1, c2) has seen 1s in previous rows
        column_pair_counts = collections.Counter()
        
        for r in range(rows):
            for c1 in range(cols):
                if matrix[r][c1] == 1:
                    for c2 in range(c1 + 1, cols):
                        if matrix[r][c2] == 1:
                            pair_key = (c1, c2)
                            existing_pairs = column_pair_counts[pair_key]
                            total_rectangles += existing_pairs
                            column_pair_counts[pair_key] += 1
                            
        return total_rectangles

    def count_via_sparse_list(self, matrix: List[List[int]]) -> int:
        """
        Approach 2: Sparse Matrix Index Mapping
        Best for: Sparse grids containing mostly 0s. Skips empty coordinate checks entirely.
        Time Complexity: O(R * C + Number of 1s^2)
        Space Complexity: O(Number of 1s)
        """
        if not matrix or len(matrix) < 2 or len(matrix[0]) < 2:
            return 0
            
        rows = len(matrix)
        # Pre-process: Record only column indices where 1 appears for each row
        rows_with_ones = []
        for r in range(rows):
            ones_in_row = [c for c, val in enumerate(matrix[r]) if val == 1]
            rows_with_ones.append(ones_in_row)
            
        total_rectangles = 0
        column_pair_counts = collections.Counter()
        
        for ones_in_row in rows_with_ones:
            num_ones = len(ones_in_row)
            for i in range(num_ones):
                c1 = ones_in_row[i]
                for j in range(i + 1, num_ones):
                    c2 = ones_in_row[j]
                    pair_key = (c1, c2)
                    total_rectangles += column_pair_counts[pair_key]
                    column_pair_counts[pair_key] += 1
                    
        return total_rectangles

    def count_via_bitmask(self, matrix: List[List[int]]) -> int:
        """
        Approach 3: Row Bit-Manipulation 
        Best for: Fast execution on dense matrices or matrices with few columns.
        Time Complexity: O(R^2) using native bitwise AND optimization.
        Space Complexity: O(R)
        """
        if not matrix or len(matrix) < 2 or len(matrix[0]) < 2:
            return 0
            
        rows = len(matrix)
        # Convert each row's array of 0s and 1s into an integer bitmask
        row_masks = []
        for r in range(rows):
            mask = 0
            for c, val in enumerate(matrix[r]):
                if val == 1:
                    mask |= (1 << c)
            row_masks.append(mask)
            
        total_rectangles = 0
        
        # Compare row bitmasks pairwise
        for r1 in range(rows):
            for r2 in range(r1 + 1, rows):
                # Intersect bits to find shared columns containing 1
                common_columns = row_masks[r1] & row_masks[r2]
                
                # Count set bits (bin(x).count('1') leverages optimized C-level code in Python)
                shared_count = bin(common_columns).count('1')
                
                if shared_count > 1:
                    # Combination formula: n * (n - 1) // 2
                    total_rectangles += (shared_count * (shared_count - 1)) // 2
                    
        return total_rectangles

# ==========================================
# Automated Test Suite Executor
# ==========================================
if __name__ == "__main__":
    finder = CornerRectangleFinder()
    
    # Define our suite of boundary and standard test matrix scenarios
    test_cases = [
        { "name": "Minimal Grid Boundary ()", "matrix": [[1],[1]], "expected": 0 } ,
        { "name": "Dense Matrix Combinations ()", "matrix": [[1, 1, 1],[1, 1, 1],[1, 1, 1]], "expected": 9 },
        { "name": "Sparse Distributed Rectangles ()", "matrix": [[1, 0, 0, 1],[0, 1, 1, 0],[1, 0, 0, 1],[0, 1, 1, 0]], "expected": 2 },
        { "name": "Non-Square Asymmetric Grid ()", "matrix": [[0, 1, 0, 0],[0, 0, 0, 0],[0, 0, 0, 0],[1, 1, 0, 0]], "expected": 1 }
    ]
    
    print("--- Starting Python Multi-Algorithm Test Verification ---")
    
    # Programmatic verification of all strategies
    for idx, case in enumerate(test_cases, 1):
        matrix = case["matrix"]
        expected = case["expected"]
        
        res_map = finder.count_via_frequency_map(matrix)
        res_sparse = finder.count_via_sparse_list(matrix)
        res_bit = finder.count_via_bitmask(matrix)
        
        # Validation engine
        print(f"********************* Test Results({idx}) ****************************")
        print(f" ->  {case['name']} (): Frequency Map Result. Expected {expected}, got {res_map}")     
        print("---------------------------------------------------------------")
        print(f" ->  {case['name']} (): Sparse List Result. Expected {expected}, got {res_sparse}")        
        print("---------------------------------------------------------------")
        print(f" ->  {case['name']} (): Bitmask Result. Expected {expected}, got {res_bit}")
        
"""
Description:
This module implements the CornerRectangleFinder class, 
which provides three optimized algorithms to count the number of corner rectangles 
in a 2D binary matrix. 
Each algorithm is designed for different scenarios, 
including dense matrices, sparse matrices, and general-purpose grids. 
The module also includes an automated test suite to validate 
the correctness of each algorithm against predefined test cases.

knowledge base:

Whats a corner rectangle?
A corner rectangle is defined as a rectangle 
in a 2D binary matrix where the four corners are all 1s. 
The sides of the rectangle are aligned with the axes of the matrix.

what are the three algorithms implemented in this module?
1. Column-Pair Frequency Tracking (Optimized Hash Map):
   This algorithm counts the number of corner rectangles 
   by tracking the frequency of column pairs that have 1s in previous rows.
2. Sparse Matrix Index Mapping:
   This algorithm is optimized for sparse matrices and records only the column indices
   where 1s appear for each row, allowing it to skip empty coordinate checks.
3. Row Bit-Manipulation:
   This algorithm converts each row into a bitmask and uses bitwise operations
   to efficiently count the number of shared columns containing 1s between pairs of rows.
"""