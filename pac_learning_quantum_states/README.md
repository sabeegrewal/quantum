# PAC-learning Quantum States

[Aaronson](https://royalsocietypublishing.org/doi/full/10.1098/rspa.2007.0113)
showed that quantum states can be PAC-learned with a linearly scaling training
set. This project demonstrates that linear scaling.

Specifically, this project will
1) compute the minimum number of measurements _m_ required to learn an _n_ qubit GHZ state for different values of _n_, and
2) plot _m_ vs. _n_.

Running this program will produce a plot similar to Figure 5 in [Experimental Learning of Quantum States](https://arxiv.org/abs/1712.00127). The learning accuracy and number of qubits can be configured by the user.

## Requirements

* Python 3

## Usage

```python
pip install -r requirements.txt
python3 main.py
```

## License

This project is licensed under the MIT License.

## Acknowledgements

Thanks to Andrea Rocchetto for getting me started on this project.

## Contact

Open an issue with any questions/comments.
