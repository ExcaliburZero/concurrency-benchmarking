import matplotlib.pyplot as plt
import pandas as pd

def main():
    benchmark = "Benchmark"
    score = "Score"
    unit = "Unit"

    custom = "custom"
    standard = "standard"

    data_file = "results.csv"
    data = pd.read_csv(data_file)

    print(data)

    benchmarks = data[benchmark].unique()

    custom_benchmarks = [b for b in benchmarks if custom in b]
    standard_benchmarks = [b.replace(custom, standard) for b in custom_benchmarks]

    for (c, s) in zip(custom_benchmarks, standard_benchmarks):
        c_score = data[data[benchmark] == c][score].iloc[0]
        s_score = data[data[benchmark] == s][score].iloc[0]

        u = data[data[benchmark] == s][unit].iloc[0]
        b = data[data[benchmark] == s][benchmark].iloc[0]

        objects = [c, s]
        y_pos = [0, 1]
        performance = [c_score, s_score]

        plt.barh(y_pos, performance, align="center", alpha=0.5)
        plt.yticks(y_pos, objects)
        plt.xlabel(u)
        plt.title(b)
 
        plt.show()

        print(c, s)
        print(c_score, s_score)

if __name__ == "__main__":
    main()
