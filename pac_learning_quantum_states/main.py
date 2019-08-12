"""Simulation of Theorem 1 from Experimental Learning of Quantum States. [1]
[1]: https://arxiv.org/abs/1712.00127

This program computes the minimum number of measurements m that achieves
a learning accuracy {EPSILON, GAMMA, DELTA} for an n qubit ghz state
for different values of n. The final output is a plot of m vs n.

Usage:
    $ python3 main.py
"""

from math import sqrt
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
import numpy as np

from constants import DELTA, EPSILON, GAMMA
from constants import HAZAN_ITER, MAX_QUBITS, MAX_STATES, MAX_TRAINING_SETS
import util

def main():
    best_m = np.zeros(shape=(MAX_QUBITS-1, MAX_STATES))
    for n in range(2, MAX_QUBITS+1):
        for state in range(1, MAX_STATES+1):
            ghz = np.zeros(shape=(2**n, 1))
            ghz[0] = 1/sqrt(2)
            ghz[(2**n)-1] = 1/sqrt(2)
            rho = np.outer(ghz, ghz)

            # Tracks measurements which distinguish our
            # state from the maximally mixed state.
            mes_distinguish = []

            # Tracks all measurement outcomes.
            mes_res = np.zeros(4**n)

            comp_mixed = np.eye(2**n)/2**n
            for j in range(0, 4**n):
                M = util.gen_e(n, j)
                obs = util.gen_obs(n, j)
                # Measure
                mes_comp_mixed = np.trace(np.matmul(obs, comp_mixed))
                mes_rho = np.trace(np.matmul(obs, rho))

                # Does the measurement distinguish them?
                if abs(mes_comp_mixed - mes_rho) > 10e-15:
                    mes_distinguish.append(j)
                mes_res[j] = np.trace(np.matmul(M, rho))
            m_max = len(mes_distinguish)

            # Generate all measurements
            # We want to make sure that every chain in the next loop
            # is averaged over the same values of m
            all_msmt = np.zeros(shape=(MAX_TRAINING_SETS, m_max))
            for j in range(0, MAX_TRAINING_SETS):
                perm = np.random.permutation(mes_distinguish)
                all_msmt[j] = perm

            idx = 0
            current_delta = 10
            m_values = list(range(1, m_max+1)) # from 0 to m_max?

            while (current_delta > DELTA and idx < m_max):
                m = m_values[idx]
                good_training_set = 0

                for i in range(0, MAX_TRAINING_SETS):
                    r = all_msmt[i, 0:m]
                    E = util.gen_e_m(n, m, r)
                    b = mes_res[r.astype(int)]

                    # Find best approximate matrix sigma
                    hypothesis = util.hazan_opt(n, m, E, b, HAZAN_ITER)

                    b_est = np.zeros(shape=m)
                    for k in range(0, m):
                        b_est[k] = np.trace(np.matmul(E[k], hypothesis))

                    count_epsilon = 0
                    for k in range(0, len(mes_distinguish)):
                        measurement = util.gen_e(n, mes_distinguish[k])
                        mes1 = np.trace(np.matmul(measurement, hypothesis))
                        mes2 = mes_res[mes_distinguish[k]]
                        if abs(mes1 - mes2) > GAMMA:
                            count_epsilon = count_epsilon + 1

                    epsilon_est = count_epsilon/len(mes_distinguish)
                    if epsilon_est < EPSILON:
                        good_training_set = good_training_set + 1

                # probability is a good training set = 1 - DELTA
                current_delta = (1-(good_training_set/MAX_TRAINING_SETS))
                print('n = %i, s = %i, m = %i - %i iterations \n'
                      % (n, state, m, MAX_TRAINING_SETS))
                idx = idx + 1
            # end of while loop
            best_m[n-2, state-1] = m

    xaxis = np.arange(2, MAX_QUBITS+1)
    yaxis = []
    err = []
    for arr in best_m:
        yaxis.append(np.mean(arr))
        err.append(np.std(arr))

    axis = plt.figure().gca()
    axis.xaxis.set_major_locator(MaxNLocator(integer=True))
    fit = np.polyfit(xaxis, yaxis, 1)
    fit_fn = np.poly1d(fit)
    plt.errorbar(xaxis, yaxis, fmt="ro", yerr=err)
    plt.plot(xaxis, fit_fn(xaxis), '--k')
    plt.xlabel("n (number of qubits)")
    plt.ylabel("m (number of measurements)")
    plt.savefig('plot.png')

if __name__ == "__main__":
    main()
