import unittest

from FindCyclesInSingleLinkList import ListNode


class TestListNode(unittest.TestCase):
    def setUp(self):
        self.head = ListNode(1, ListNode(2, ListNode(3, ListNode(4))))

    def test_find_cycle_returns_false_for_linear_list(self):
        self.assertFalse(self.head.find_cycle())

    def test_find_cycle_returns_true_for_cyclic_list(self):
        tail = self.head.next.next.next
        tail.next = self.head.next

        self.assertTrue(self.head.find_cycle())

    def test_iteration_length_and_contains(self):
        self.assertEqual(list(self.head), [1, 2, 3, 4])
        self.assertEqual(len(self.head), 4)
        self.assertIn(3, self.head)
        self.assertNotIn(9, self.head)

    def test_getitem_and_setitem(self):
        self.assertEqual(self.head[2], 3)
        self.head[2] = 99
        self.assertEqual(self.head[2], 99)

        with self.assertRaises(IndexError):
            _ = self.head[10]

    def test_reversed_returns_values_in_reverse_order(self):
        self.assertEqual(list(reversed(self.head)), [4, 3, 2, 1])

    def test_add_concatenates_two_lists(self):
        other = ListNode(5, ListNode(6))
        combined = self.head + other

        self.assertEqual(list(combined), [1, 2, 3, 4, 5, 6])

    def test_mul_repeats_the_entire_list(self):
        repeated = self.head * 2

        self.assertEqual(list(repeated), [1, 2, 3, 4, 1, 2, 3, 4])

    def test_mul_with_zero_returns_none(self):
        self.assertIsNone(self.head * 0)


if __name__ == "__main__":
    unittest.main()
