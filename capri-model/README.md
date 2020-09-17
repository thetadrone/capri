# Capri: A performance model used as bid advisor in spot market for data analytics

## Description

Some documentation is listed below.

- [Analyzing the bidding queueing model](docs/papers/main.pdf)
- [Estimation using simulation](docs/slides/CapriEstimation_02.pdf)
- [Multi-class data analysis](docs/slides/ibiza_multi_02.pdf)
- [Experimental data analysis](docs/slides/results_2.pdf)

Capri is written in Java.

The interface file is given [here](capri/src/capri/interfaces/Capri.java).

An [example](capri/src/capri/test/CapriTest.java) is provided. It uses sample experimental [data](capri/bidData.txt). An [example](capri/src/capri/test/CapriTestSmooth.java) with data smoothing is also provided. Muti-class [examples](capri/src/capri/test/) are included.

Default configuration file is in [capri.cfg](capri/capri.cfg).

The model assumes a Beta distribution for the bids, characterized by two parameters: `<alpha>` and `<beta>`. Furthermore, the model uses an M/M/1 queue, characterized by the server utilization `<rho>`. Thus, the model tracks dynamically the triplet {`<alpha>`, `<beta>`, `<rho>`} as it learns of new data collected at job completions, namely the total queueing time `<waitTime>` and the total job time in the system `<respTime>`.

The Capri model uses a combination of an M/M/1 bribing queue, an extended Kalman filter, moment distribution fitting, and Newton-Raphson equation solver.


## Usage

Create an instance of a Capri model using the CapriFactory as shown below.
```bash
Capri capriModel = CapriFactory.create();
```

All configuration parameters should be set in the capri.cfg file. If other file name `<cfgFileName>` is used, then the filename should be passed as an argument to the Factory.
```bash
Capri capriModel = CapriFactory.create(<cfgFileName>);
```

For every data point, i.e. job completion, the state of the model may be dynamically updated with the measurements `<bid>`, `<waitTime>`, `<respTime>`, along with an `<id>` for the job using the update method.
```bash
capriModel.update(<id>, <bid>, <waitTime>, <respTime>);
```

To have the model suggest a bid value in order to attain a given `<targetSlowDown>` for a job with a given `<servTime>`, the following method is used.

```bash
capriModel.getBid(<targetSlowDown>, <servTime>);
```
And, for the unconditional service time, one uses.
```bash
capriModel.getBid(<targetSlowDown>);
```

To have the model estimate the slow down of a job with `<servTime>` at a bid value `<bid>`, the following method is used,
```bash
capriModel.getSlowDown(<bid>, <servTime>, <lowHighRange>);
```
where `<lowHighRange>` is an array of size two where a low and high slow down values are retuned for a percentile range, as specified in the configuration file.
For the equivalent unconditional service time, one uses.
```bash
capriModel.getSlowDown(<bid>, <lowHighRange>);
```

The model parameter triplet {`<alpha>`, `<beta>`, `<rho>`} may be obtained at any time through the following method.
```bash
capriModel.getModelParameters();
```
