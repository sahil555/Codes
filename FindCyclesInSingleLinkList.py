"""
This module defines a ListNode class for a singly linked list 
and provides a method to detect cycles in the list.
The ListNode class includes various utility methods for 
list manipulation and inspection. The find_cycle method uses 
Floyd's Cycle Detection Algorithm (Tortoise and Hare) to 
determine if a cycle exists in the list.
"""

class ListNode:
    def __init__(self, value=0, next=None):
        self.value = value
        self.next = next
        
    def __repr__(self):
        return f"ListNode({self.value})"
    
    def __str__(self):
        return str(self.value)
    
    def __eq__(self, other):
        if isinstance(other, ListNode):
            return self.value == other.value and self.next == other.next
        return False
    
    def __hash__(self):
        return hash((self.value, self.next))
    
    def __iter__(self):
        current = self
        while current is not None:
            yield current.value
            current = current.next
            
    def __len__(self):
        length = 0
        current = self
        while current is not None:
            length += 1
            current = current.next
        return length
    
    def __getitem__(self, index):
        if index < 0:
            raise IndexError("Index must be non-negative")
        current = self
        for _ in range(index):
            if current is None:
                raise IndexError("Index out of bounds")
            current = current.next
        if current is None:
            raise IndexError("Index out of bounds")
        return current.value
    
    def __setitem__(self, index, value):
        if index < 0:
            raise IndexError("Index must be non-negative")
        current = self
        for _ in range(index):
            if current is None:
                raise IndexError("Index out of bounds")
            current = current.next
        if current is None:
            raise IndexError("Index out of bounds")
        current.value = value
        
    def __contains__(self, value):
        current = self
        while current is not None:
            if current.value == value:
                return True
            current = current.next
        return False
    
    def __reversed__(self):
        stack = []
        current = self
        while current is not None:
            stack.append(current.value)
            current = current.next
        return reversed(stack)
    
    def __add__(self, other):
        if not isinstance(other, ListNode):
            raise TypeError("Can only concatenate ListNode (not '{}') to ListNode".format(type(other).__name__))
        new_head = ListNode(self.value)
        current_new = new_head
        current_self = self.next
        while current_self is not None:
            current_new.next = ListNode(current_self.value)
            current_new = current_new.next
            current_self = current_self.next
        current_other = other
        while current_other is not None:
            current_new.next = ListNode(current_other.value)
            current_new = current_new.next
            current_other = current_other.next
        return new_head
    
    def __mul__(self, times):
        if not isinstance(times, int) or times < 0:
            raise ValueError("Times must be a non-negative integer")
        if times == 0:
            return None
        # copy the list once to initialize
        current_self = self
        new_head = ListNode(current_self.value)
        current_new = new_head
        current_self = current_self.next
        while current_self is not None:
            current_new.next = ListNode(current_self.value)
            current_new = current_new.next
            current_self = current_self.next

        # append (times - 1) additional copies
        for _ in range(times - 1):
            curr = self
            while curr is not None:
                current_new.next = ListNode(curr.value)
                current_new = current_new.next
                curr = curr.next
        return new_head
    
    def find_cycle(self):
        slow = self
        fast = self
        while fast is not None and fast.next is not None:
            slow = slow.next
            fast = fast.next.next
            if slow is fast:
                return True
        return False
        
        
    
if __name__ == "__main__":
    # Example usage and test cases
    node1 = ListNode(1)
    node2 = ListNode(2)
    node3 = ListNode(3)
    node4 = ListNode(4)
    node5 = ListNode(5)
    node6 = ListNode(6)
    node7 = ListNode(7)
    node8 = ListNode(8)
    node9 = ListNode(9)
    node10 = ListNode(10)
    node11 = ListNode(11)
    node12 = ListNode(4)
    
    
    # Creating a linked list: 1 -> 2 -> 3 -> 4
    node1.next = node2
    node2.next = node3
    node3.next = node4
    node4.next = node5
    node5.next = node6
    node6.next = node7
    node7.next = node8
    node8.next = node9
    node9.next = node10
    node10.next = node11
    node11.next = node12

    print("Linked List:", list(node1))  # Output: [1, 2, 3, 4]
    
    # Testing find_cycle method
    print("Cycle Detected:", node1.find_cycle())  # Output: False
    
    # Creating a cycle: 4 -> 2
    node4.next = node2
    
    print("Cycle Detected after creating a cycle:", node1.find_cycle())  # Output: True