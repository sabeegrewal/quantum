""" Constants used in util.py and main.py """

import numpy as np

ID = np.array([[1, 0], [0, 1]])
"""Identity matrix."""

S_X = np.array([[0, 1], [1, 0]])
"""Pauli-X matrix."""

S_Y = np.array([[0, -1j], [1j, 0]])
"""Pauli-Y matrix."""

S_Z = np.array([[1, 0], [0, -1]])
"""Pauli-Z matrix."""

GAMMA = 0.2
"""Learning parameter from Theorem 1."""

DELTA = 0.2
"""Learning parameter from Theorem 1."""

EPSILON = 0.15
"""Learning parameter from Theorem 1."""

MAX_STATES = 10
"""Number of different states."""

MAX_TRAINING_SETS = 10
"""Number of different random training sets."""

MAX_QUBITS = 4
"""Max number of qubits."""

HAZAN_ITER = 200
"""Number of iterations of the optimization algorithm."""
