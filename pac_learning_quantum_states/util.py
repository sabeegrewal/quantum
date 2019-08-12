"""Utility functions used by main.py.

This module contains Hazan's optimization algorithm and helper functions
which generate measurements.
"""

import itertools
import numpy as np
from numpy import linalg

from constants import ID, S_X, S_Y, S_Z

def all_measurements(n):
    """This function generates all possible combinations of measurements given
    the following observables:
        1 = ID  --  2 = sigma_x  --  3 = sigma_y  --  4 = sigma_z.
    In other words, it makes a 4**n x n tables with all permutations.

    Args:
        n: number of qubits

    Returns:
        Matrix of measurements combination
    """
    return np.array(list(itertools.product([0, 1, 2, 3], repeat=n)))

def gen_e(n, idx):
    """This function generates the first element E_i of a two-outcome POVM
    measurement. Here E_m_1 + E_m_2 = ID.

    Args:
        n: number of qubits
        idx: row number of table of measurements

    Returns:
        Cell array with POVM elements
    """
    a_m = all_measurements(n)
    pauli = np.array([ID, S_X, S_Y, S_Z])
    if n == 1:
        return (np.eye(2**n) + pauli[a_m[idx, n-1]]) / 2
    # Initializes observable by taking Kronecker tensor
    # product of two pauli measurements.
    obs = np.kron(pauli[a_m[idx, n-2]], pauli[a_m[idx, n-1]])
    if n > 2:
        # Generates observable
        for j in range(3, n+1):
            obs = np.kron(pauli[a_m[idx, n-j]], obs)
    # Generate POVM elements. If
    #   E_1 + E_2 = ID,
    #   E_1 - E_2 = Observable, then
    #   E_1 = (ID + Observable)/2
    return (np.eye(2**n) + obs)/2

def gen_obs(n, idx):
    """This function generates the observable O which is a subset of the gen_e
    function.

    Args:
        n: number of qubits
        idx: row number of table of measurements

    Returns:
        Cell array with Observation matrix
    """
    a_m = all_measurements(n)
    pauli = np.array([ID, S_X, S_Y, S_Z])
    obs = np.kron(pauli[a_m[idx, n-2]], pauli[a_m[idx, n-1]])
    if n > 2:
        # Generates observable
        for j in range(3, n+1):
            obs = np.kron(pauli[a_m[idx, n-j]], obs)

    return obs

def gen_e_m(n, m, r):
    """This function generates the first elements E_i of m two-outcome POVM
    measurements. Here E_m_1 + E_m_2 = ID.

    Args:
        n: number of qubits
        m: number of training measurments
        r: selection of random measurements e.g. [XZYZ] for 4 qubits

    Returns:
        Cell array with POVM elements
    """
    # Generate random measurements
    a_m = all_measurements(n)
    pauli = np.array([ID, S_X, S_Y, S_Z])

    # Preallocation. E is a m x 2**n x 2**n tensor.
    measurements = np.array(np.zeros(shape=(m, 2**n, 2**n)))

    tmp = None
    if n == 1:
        for i in range(0, m):
            measurements[i] = pauli[a_m[int(r[i]), n-1]]
    else:
        for i in range(0, m):
            tmp = pauli[a_m[int(r[i]), n-1]]
            for j in range(2, n+1):
                tmp = np.kron(pauli[a_m[int(r[i]), n-j]], tmp)
            measurements[i] = tmp

    for i in range(0, m):
        measurements[i] = (np.eye(2**n) + measurements[i])/2

    return measurements

def hazan_opt(n, m, measurements, b, k_max):
    """This function finds a hypothesis state S given a training set {E_i }
    of m measurements as well as approximate values of Tr(E*rho)=b via the
    Hazan algorithm (Elad Hazan,"Sparse Approximate Solutions to SemiDefinite
    Programs").

    Args:
        n: number of qubits
        m: number of training measurments
        measurements: cell array with POVM elements
        b: vector of the measuremnts b = Tr(E*rho)
        k_max: number of iterations of the algorithm (if not specified
               default value is set to 100)

    Returns:
        Estimated sigma -> min_rho (Sum(Tr(E_i*rho)-b_i)^2)
    """
    # Initialize hypothesis state to the maximally mixed state which
    # is our guess after 0 iterations of the the optimization problem.
    hypothesis = np.eye(2**n) / 2**n

    for k in range(0, k_max):
        # Compute the smallest eignevector v of Grad(f(S))
        g_f = np.zeros(2**n)
        for i in range(0, m):
            g_f = g_f + (np.trace(np.matmul(measurements[i], hypothesis)) - b[i])*measurements[i]

        g_f = 2*g_f
        eigen_values, eigen_vectors = linalg.eig(g_f)
        idx = np.argsort(eigen_values)
        eigen_vectors = eigen_vectors[:, idx]
        first = eigen_vectors[0]

        # Compute alpha
        alpha = 1/(k+1)

        # Estimate S
        tmp = hypothesis + (alpha*(np.outer(first, first) - hypothesis))
        hypothesis = tmp

    return hypothesis
