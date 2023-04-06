package de.uniregensburg.iamreportingmodule.core.util;

import de.uniregensburg.iamreportingmodule.core.exception.FileException;
import de.uniregensburg.iamreportingmodule.data.entity.CsvAggregationMethod;
import de.uniregensburg.iamreportingmodule.data.entity.FileDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Utility to query csv files
 *
 * @author
 */
public class CsvUtil {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FileDataSource fileDataSource;

    /**
     *
     * @param fileDataSource
     */
    public CsvUtil(FileDataSource fileDataSource) {
        this.fileDataSource = fileDataSource;
    }

    /**
     * Returns result of measurement specified by attributes header, column, delimiter and aggregation method
     *
     * @param attributes
     * @return
     * @throws FileException
     */
    public Result measure(Map<String, String> attributes) throws FileException {
        logger.info("Getting attributes");
        // check header
        if (attributes.get("csvHeader") == null) {
            logger.info("No header attribute provided");
            throw new FileException("No header attribute provided");
        }
        // get header
        boolean header = Boolean.parseBoolean(attributes.get("csvHeader"));

        // get column
        String columnName = "";
        int columnIndex = -1;
        // header
        if (header) {
            // check column name
            if (attributes.get("csvColumnName") == null) {
                logger.info("No column name attribute provided");
                throw new FileException("No column name attribute provided");
            }
            // get column name
            columnName = attributes.get("csvColumnName");
        // no header
        } else {
            // check column index
            if (attributes.get("csvColumnIndex") == null) {
                logger.info("No column index attribute provided");
                throw new FileException("No column index attribute provided");
            }
            // get column index
            try {
                columnIndex = Integer.parseInt(attributes.get("csvColumnIndex"));
            } catch (NumberFormatException e) {
                logger.info("Cannot convert column index to integer: " + e.getMessage());
                throw new FileException("Cannot convert column index to integer: " + e.getMessage());
            }
        }
        // check delimiter
        if (attributes.get("csvDelimiter") == null) {
            logger.info("No delimiter attribute provided");
            throw new FileException("No delimiter attribute provided");
        }
        // get delimiter
        String delimiter = attributes.get("csvDelimiter");
        // check aggregation method
        if (attributes.get("csvAggregationMethod") == null) {
            logger.info("No delimiter attribute provided");
            throw new FileException("No delimiter attribute provided");
        }
        // get aggregation method
        CsvAggregationMethod aggregationMethod = CsvAggregationMethod.valueOf(attributes.get("csvAggregationMethod"));

        // get file
        fileDataSource.getFile();

        // read in file row by row
        String line;
        List<List<String>> lines = new ArrayList<>();
        try (ByteArrayInputStream is = new ByteArrayInputStream(fileDataSource.getFile()); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while((line = br.readLine()) != null){
                List<String> rowValues = Arrays.asList(line.split(delimiter));
                lines.add(rowValues);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        // check if rows exist
        if (lines.size() == 0) {
            logger.info("No data found");
            throw new FileException("No data found");
        }

        // header
        if (header) {
            // get header row
            List<String> headerLine = lines.get(0);
            // check if column name exists in header
            if (headerLine.contains(columnName)) {
                columnIndex = headerLine.indexOf(columnName);
            } else {
                logger.info("No column with name " + columnName + " found");
                throw new FileException("No column with name " + columnName + " found");
            }
            // remove header line
            lines.remove(0);
        }

        // get values of specified column
        List<String> columnValues = new ArrayList<>();
        int i = columnIndex;
        try {
            lines.forEach(l -> columnValues.add(l.get(i)));
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("Column not found" + e.getMessage());
            throw new FileException("Column not found: " + e.getMessage());
        }

        // check if column contains values
        if (columnValues.size() == 0) {
            logger.info("No data found");
            throw new FileException("No data found");
        }

        // aggregate columns to result using specified aggregation method
        Result result;
        try {
            result = switch (aggregationMethod) {
                // count rows
                case COUNT:
                    yield new Result(new BigDecimal(columnValues.size()));
                // build sum of values of all rows
                case SUM:
                    BigDecimal sum = null;
                    Iterator<String> itSum = columnValues.iterator();
                    if(itSum.hasNext()){
                        sum = new BigDecimal(itSum.next());
                        while(itSum.hasNext()){
                            sum = sum.add(new BigDecimal(itSum.next()));
                        }
                    }
                    yield new Result(sum);
                // get minimum value
                case MINIMUM:
                    BigDecimal min = null;
                    Iterator<String> itMinimum = columnValues.iterator();
                    if(itMinimum.hasNext()){
                        min = new BigDecimal(itMinimum.next());
                        while(itMinimum.hasNext()){
                            min = min.min(new BigDecimal(itMinimum.next()));
                        }
                    }
                    yield new Result(min);
                // get maximum value
                case MAXIMUM:
                    BigDecimal max = null;
                    Iterator<String> itMaximum = columnValues.iterator();
                    if(itMaximum.hasNext()){
                        max = new BigDecimal(itMaximum.next());
                        while(itMaximum.hasNext()){
                            max = max.max(new BigDecimal(itMaximum.next()));
                        }
                    }
                    yield new Result(max);
                // get average value
                case AVERAGE:
                    BigDecimal numerator = null;
                    Iterator<String> itNumerator = columnValues.iterator();
                    if(itNumerator.hasNext()){
                        numerator = new BigDecimal(itNumerator.next());
                        while(itNumerator.hasNext()){
                            numerator = numerator.add(new BigDecimal(itNumerator.next()));
                        }
                    }
                    BigDecimal denominator = new BigDecimal(columnValues.size());
                    if (denominator.equals(BigDecimal.ZERO) || numerator == null) {
                        yield new Result(null);
                    } else {
                        yield new Result(numerator.divide(denominator, MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_UP));
                    }
                // get median value
                case MEDIAN:
                    ArrayList<BigDecimal> bigDecimals = new ArrayList<>();
                    columnValues.forEach(v -> bigDecimals.add(new BigDecimal(v))); // convert to BigDecimal
                    bigDecimals.sort(BigDecimal::compareTo); // sort bigDecimals
                    BigDecimal median;
                    if (bigDecimals.size() == 0) {
                        median = null;
                    } else if (bigDecimals.size() % 2 == 1) {
                        median = bigDecimals.get((bigDecimals.size() + 1) / 2 - 1 );
                    } else {
                        median = bigDecimals.get(bigDecimals.size() / 2 - 1);
                        median = median.add(bigDecimals.get(bigDecimals.size() / 2));
                        median = median.divide(new BigDecimal(2), MathContext.DECIMAL128.getPrecision(), RoundingMode.HALF_UP);
                    }
                    yield new Result(median);
            };
        } catch (Exception e) {
            logger.info("Error during aggregation: " + e.getMessage());
            throw new FileException("Error during aggregation: " + e.getMessage());
        }

        // return result
        return result;
    }
}
